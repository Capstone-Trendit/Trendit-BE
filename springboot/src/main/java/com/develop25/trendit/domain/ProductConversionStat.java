package com.develop25.trendit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "product_conversion_stat")
public class ProductConversionStat {

    @EmbeddedId
    private ProductWindowKey id;

    private Long searchCount;
    private Long purchaseCount;
    private Double conversionRate;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}
