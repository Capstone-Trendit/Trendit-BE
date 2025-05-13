package org.example;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class ConsumerEventSink implements SinkFunction<ConsumerEvent> {

    @Override
    public void invoke(ConsumerEvent value, Context context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsedTime = LocalDateTime.parse(value.getEventTime());

        System.out.println("==== 소비자 행동 이벤트 ====");
        System.out.println("소비자 ID: " + value.getConsumerId());
        System.out.println("상품 ID: " + value.getProductId());
        System.out.println("상품명: " + value.getProductName());
        System.out.println("이벤트 타입: " + value.getEventType());
        System.out.println("가격: " + value.getPrice());
        System.out.println("시간: " + formatter.format(parsedTime));
        System.out.println("============================");
    }
}