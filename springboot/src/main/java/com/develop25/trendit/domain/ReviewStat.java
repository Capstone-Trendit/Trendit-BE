package com.develop25.trendit.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_stat")
public class ReviewStat {

    @Id
    private UUID reviewId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String sentiment;

    @Column(columnDefinition = "TEXT")
    private String originalComment;

    private LocalDateTime analyzedAt;
}