package com.develop25.trendit.dto;

import com.develop25.trendit.domain.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {
    private String name;
    private Double price;
    private Long count;
    //private Long userId;

    public Product toEntity() {
        //데이터를 받아서 엔티티 클래스의 객체로 반환함
        return new Product(name, price, count);
    }

}
