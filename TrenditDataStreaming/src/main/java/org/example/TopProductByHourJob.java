package org.example;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TopProductByHourJob {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<ConsumerEvent> kafkaSource = KafkaSource.<ConsumerEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-consumer-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new ConsumerEventDeserializationSchema())
                .build();

        DataStream<ConsumerEvent> stream = env
                .fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
                .filter(event -> event != null && "purchase".equalsIgnoreCase(event.getEventType()));

        // 테스팅을 위해 최근 20초간 top 5 상품 8초로 출력
        stream
                .keyBy(ConsumerEvent::getProductId)
                .window(SlidingProcessingTimeWindows.of(Time.minutes(1), Time.seconds(10)))
                .aggregate(new CountAggregator(), new CountResultWindowFunction())
                .windowAll(SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(8)))
                .process(new TopNProcessFunction(5))
                .addSink(new PrintSinkFunction<>());

        env.execute("Processing Time Sliding Window Top 5 Products");
    }

    public static class CountAggregator implements AggregateFunction<ConsumerEvent, Long, Long> {
        @Override
        public Long createAccumulator() { return 0L; }
        @Override
        public Long add(ConsumerEvent value, Long acc) { return acc + 1; }
        @Override
        public Long getResult(Long acc) { return acc; }
        @Override
        public Long merge(Long a, Long b) { return a + b; }
    }

    public static class CountResultWindowFunction extends ProcessWindowFunction<Long, Tuple2<Long, Long>, Long, TimeWindow> {
        @Override
        public void process(Long productId, Context ctx, Iterable<Long> counts, Collector<Tuple2<Long, Long>> out) {
            out.collect(Tuple2.of(productId, counts.iterator().next()));
        }
    }

    public static class TopNProcessFunction extends ProcessAllWindowFunction<Tuple2<Long, Long>, String, TimeWindow> {
        private final int topSize;

        public TopNProcessFunction(int topSize) {
            this.topSize = topSize;
        }

        @Override
        public void process(Context ctx, Iterable<Tuple2<Long, Long>> input, Collector<String> out) {
            // 1. 상품 ID별로 집계 합치기
            Map<Long, Long> mergedCounts = new HashMap<>();
            for (Tuple2<Long, Long> entry : input) {
                mergedCounts.merge(entry.f0, entry.f1, Long::max);  // 최신값 유지 (혹은 += 누적 가능)
            }

            // 2. 정렬 후 Top 5 추출
            List<Map.Entry<Long, Long>> sorted = mergedCounts.entrySet().stream()
                    .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                    .limit(topSize)
                    .collect(Collectors.toList());

            // 3. 결과 문자열 구성
            StringBuilder result = new StringBuilder("\n🕒 [ProcessingTime] 최근 1분 Top 5 상품 (10초 단위로 갱신)\n");
            int rank = 1;
            for (Map.Entry<Long, Long> entry : sorted) {
                result.append(String.format("%d위: 상품 ID %d (판매 수: %d)\n", rank++, entry.getKey(), entry.getValue()));
            }

            result.append("윈도우 범위: ")
                    .append(LocalDateTime.ofInstant(Instant.ofEpochMilli(ctx.window().getStart()), ZoneId.systemDefault()))
                    .append(" ~ ")
                    .append(LocalDateTime.ofInstant(Instant.ofEpochMilli(ctx.window().getEnd()), ZoneId.systemDefault()))
                    .append("\n");

            out.collect(result.toString());
        }
    }
}





