package com.develop25.trendit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerEventDto {
    private String eventType;     // "purchase", "search", "review"
    private String userId;
    private Long productId;
    private Integer price;        // optional
    private String comment;       // optional
    private LocalDateTime timestamp;
}