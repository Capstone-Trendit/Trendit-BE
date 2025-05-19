package org.example;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.sql.Timestamp;

public class ConversionRateJob {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<ConsumerEvent> kafkaSource = KafkaSource.<ConsumerEvent>builder()
                .setBootstrapServers("localhost:9092")
                .setTopics("user-events")
                .setGroupId("flink-consumer-group")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new ConsumerEventDeserializationSchema())
                .build();

        DataStream<ConsumerEvent> sourceStream = env
                .fromSource(kafkaSource, org.apache.flink.api.common.eventtime.WatermarkStrategy.noWatermarks(), "Kafka Source")
                .filter(event -> event != null);

        DataStream<Tuple2<Long, ProductCVR>> searchStream = sourceStream
                .filter(event -> "search".equalsIgnoreCase(event.getEventType()))
                .map((MapFunction<ConsumerEvent, Tuple2<Long, ProductCVR>>) event -> Tuple2.of(event.getProductId(), new ProductCVR(event.getProductId(), 1, 0, 0.0, 0L)))
                .returns(TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<Tuple2<Long, ProductCVR>>(){}));

        DataStream<Tuple2<Long, ProductCVR>> purchaseStream = sourceStream
                .filter(event -> "purchase".equalsIgnoreCase(event.getEventType()))
                .map((MapFunction<ConsumerEvent, Tuple2<Long, ProductCVR>>) event -> Tuple2.of(event.getProductId(), new ProductCVR(event.getProductId(), 0, 1, 0.0, 0L)))
                .returns(TypeInformation.of(new org.apache.flink.api.common.typeinfo.TypeHint<Tuple2<Long, ProductCVR>>(){}));

        searchStream.union(purchaseStream)
                .keyBy(t -> t.f0)
                .window(SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(8)))
                .aggregate(new ProductCVRAggregator(), new ProductCVRWindowAppender())
                .addSink(JdbcSink.sink(
                        "INSERT INTO product_conversion_stat (product_id, search_count, purchase_count, conversion_rate, window_start) " +
                                "VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE search_count = VALUES(search_count), purchase_count = VALUES(purchase_count), conversion_rate = VALUES(conversion_rate)",
                        (ps, cvr) -> {
                            ps.setLong(1, cvr.productId);
                            ps.setLong(2, cvr.searchCount);
                            ps.setLong(3, cvr.purchaseCount);
                            ps.setDouble(4, cvr.conversionRate);
                            ps.setTimestamp(5, new Timestamp(cvr.windowStart));
                        },
                        JdbcExecutionOptions.builder()
                                .withBatchSize(1)
                                .withMaxRetries(3)
                                .build(),
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://trendit.cre6kequcil6.ap-northeast-2.rds.amazonaws.com:3306/trendit?useSSL=false&serverTimezone=Asia/Seoul")
                                .withDriverName("com.mysql.cj.jdbc.Driver")
                                .withUsername("admin")
                                .withPassword("aa08230214")
                                .build()       
                ));

        env.execute("Real-time Conversion Rate to MySQL Job");
    }

    public static class ProductCVRAggregator implements AggregateFunction<Tuple2<Long, ProductCVR>, ProductCVR, ProductCVR> {
        @Override
        public ProductCVR createAccumulator() { return new ProductCVR(); }

        @Override
        public ProductCVR add(Tuple2<Long, ProductCVR> value, ProductCVR acc) {
            acc.productId = value.f0;
            acc.searchCount += value.f1.searchCount;
            acc.purchaseCount += value.f1.purchaseCount;
            return acc;
        }

        @Override
        public ProductCVR getResult(ProductCVR acc) {
            acc.conversionRate = (acc.searchCount > 0) ? (acc.purchaseCount * 100.0 / acc.searchCount) : 0.0;
            return acc;
        }

        @Override
        public ProductCVR merge(ProductCVR a, ProductCVR b) {
            a.searchCount += b.searchCount;
            a.purchaseCount += b.purchaseCount;
            a.conversionRate = (a.searchCount > 0) ? (a.purchaseCount * 100.0 / a.searchCount) : 0.0;
            return a;
        }
    }

    public static class ProductCVRWindowAppender extends ProcessWindowFunction<ProductCVR, ProductCVR, Long, TimeWindow> {
        @Override
        public void process(Long key, Context ctx, Iterable<ProductCVR> elements, Collector<ProductCVR> out) {
            ProductCVR result = elements.iterator().next();
            result.windowStart = ctx.window().getStart();
            out.collect(result);
        }
    }

    public static class ProductCVR {
        public long productId;
        public long searchCount;
        public long purchaseCount;
        public double conversionRate;
        public long windowStart;

        public ProductCVR() {}

        public ProductCVR(long productId, long searchCount, long purchaseCount, double conversionRate, long windowStart) {
            this.productId = productId;
            this.searchCount = searchCount;
            this.purchaseCount = purchaseCount;
            this.conversionRate = conversionRate;
            this.windowStart = windowStart;
        }
    }
}
