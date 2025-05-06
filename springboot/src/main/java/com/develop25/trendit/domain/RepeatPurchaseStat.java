package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "repeat_purchase_stat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
