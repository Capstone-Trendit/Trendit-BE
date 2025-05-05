package com.develop25.trendit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "top_product_stat")
public class TopProductStat {

    @EmbeddedId
    private ProductWindowKey id;

    private Long purchaseCount;
    private Double revenue;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}