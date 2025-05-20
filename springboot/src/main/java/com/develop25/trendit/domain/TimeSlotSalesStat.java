package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Table(name = "time_slot_sales_stat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotSalesStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statId;

    private LocalDate statDate;
    private int hourOfDay;

    private Long salesCount;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}