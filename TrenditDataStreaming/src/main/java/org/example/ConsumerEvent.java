package org.example;

import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerEvent implements Serializable {
    private String consumerId;
    private Long productId;
    private String productName;
    private String eventType;
    private Double price;
    private String  eventTime;
}