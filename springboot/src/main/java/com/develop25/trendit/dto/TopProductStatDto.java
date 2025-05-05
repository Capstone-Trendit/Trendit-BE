package com.develop25.trendit.dto;

import lombok.Data;

@Data
public class TopProductStatDto {
    private Long productId;
    private String productName;
    private Long purchaseCount;
    private Double conversionRate;
}