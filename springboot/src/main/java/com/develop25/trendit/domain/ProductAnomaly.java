package com.develop25.trendit.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_anomaly")
public class ProductAnomaly {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String anomalyType;      // "drop", "spike"
    private LocalDateTime detectedAt;
}