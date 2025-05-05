package com.develop25.trendit.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "consumer_event")
public class ConsumerEvent {

    @Id
    private UUID eventId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String eventType;
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime ts;
}
