package com.develop25.trendit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductStatDto {
    private Long productId;
    private String productName;
    private Long salesVolume;
    private Long ranking;
}