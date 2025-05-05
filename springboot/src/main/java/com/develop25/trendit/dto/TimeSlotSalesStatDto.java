package com.develop25.trendit.dto;

import lombok.Data;

@Data
public class TimeSlotSalesStatDto {
    private String hourSlot;       // ex: "14:00-15:00"
    private Long salesCount;
}
