package com.develop25.trendit.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "repeat_purchase_stat")
public class RepeatPurchaseStat {

    @EmbeddedId
    private ProductWindowKey id;

    private Long uniqueBuyers;
    private Long repeatBuyers;
    private Double repeatRate;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;
}
