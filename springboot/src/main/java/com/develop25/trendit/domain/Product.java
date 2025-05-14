package com.develop25.trendit.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private Double price;
    private Long count;

    private Long salesVolume;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    //빌더로 생성자 초기화하는 데 이 초기화를 디폴트로 하게끔 하기(에러나서 수정했는 데 잘못된거면 빼겠습니다.)
    @Builder.Default
    @OneToMany(mappedBy = "product")
    private List<ConsumerEvent> events = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_tag",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    public Product(String name, Double price, Long count){
        this.name = name;
        this.price = price;
        this.count = count;
    }

    //상품 수정을 위한 함수(상품 수정 api에서 사용)
    public void patch(Product product) {
        if(product.name != null){
            this.name = product.name;
        }
        if(product.price != null){
            this.price = product.price;
        }
        if(product.count != null){
            this.count = product.count;
        }
    }
}