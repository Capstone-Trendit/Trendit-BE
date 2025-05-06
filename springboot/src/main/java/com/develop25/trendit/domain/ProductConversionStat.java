package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_conversion_stat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
