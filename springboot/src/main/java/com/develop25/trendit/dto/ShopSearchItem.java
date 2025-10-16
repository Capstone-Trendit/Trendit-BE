package com.develop25.trendit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//네이버 검색 api로 json을 받아올 건데 그를 위한 자료형이라고 봐도 됨, 이걸로 dto를 만들거임
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopSearchItem(
        String title,
        String link,
        String image,
        String lprice,
        String hprice,
        String mallName,
        String productId,
        Integer productType,
        String brand,
        String maker,
        String category1,
        String category2,
        String category3,
        String category4
) {}
