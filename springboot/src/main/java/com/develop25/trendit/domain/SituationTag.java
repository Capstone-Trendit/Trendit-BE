package com.develop25.trendit.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class SituationTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String situation; // ex. "비가 오는 날 먹기 좋은 음식"

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> keywords; // ex. ["전", "치킨", "회"]
}
