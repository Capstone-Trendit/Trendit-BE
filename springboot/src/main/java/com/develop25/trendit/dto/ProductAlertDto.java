package com.develop25.trendit.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductAlertDto {
    private Long productId;
    private String productName;
    private String alertType;       // "급등", "급감", "전환율 저하" 등
    private String message;
    private LocalDateTime detectedAt;
}