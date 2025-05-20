package com.develop25.trendit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlotSalesStatDto {
    private int hourOfDay;
    private Long salesCount;
}
