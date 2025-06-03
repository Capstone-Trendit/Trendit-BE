package com.develop25.trendit.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductStat {

    @EmbeddedId
    private ProductWindowKey id;

    private Long salesVolume;

    private Long ranking;
}
