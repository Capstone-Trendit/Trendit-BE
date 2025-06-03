package com.develop25.trendit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSearchStatDto {
    private Long productId;
    private String productName;
    private Long searchVolume;
    private Long ranking;
}