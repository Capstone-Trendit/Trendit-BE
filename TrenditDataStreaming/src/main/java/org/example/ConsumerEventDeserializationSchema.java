package org.example;


import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ConsumerEventDeserializationSchema implements DeserializationSchema<ConsumerEvent> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ConsumerEvent deserialize(byte[] message) throws IOException {
        try {
            return mapper.readValue(message, ConsumerEvent.class);
        } catch (Exception e) {
            System.err.println("역직렬화 실패: " + new String(message));
            e.printStackTrace();
            return null; // 이후 filter 처리 필요
        }
    }

    @Override
    public boolean isEndOfStream(ConsumerEvent nextElement) {
        return false;
    }

    @Override
    public TypeInformation<ConsumerEvent> getProducedType() {
        return TypeInformation.of(ConsumerEvent.class);
    }
}
