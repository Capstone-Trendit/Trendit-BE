package com.develop25.trendit.domain;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Lob
    @Column(columnDefinition = "LONGBLOB") // MySQL 기준
    private byte[] image;

    @OneToOne
    @JoinColumn(name = "product_id") // Image 테이블에 외래 키로 product_id 생성
    private Product product;
}
