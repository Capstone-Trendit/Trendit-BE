package com.develop25.trendit.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "time_slot_sales_stat")
public class TimeSlotSalesStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statId;

    private int hourOfDay; // 0~23

    private Long totalSalesCount;
    private Double totalRevenue;

    private LocalDate statDate;
}
