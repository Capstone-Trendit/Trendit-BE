package com.develop25.trendit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

//네이버 검색 api dto
@JsonIgnoreProperties(ignoreUnknown = true)
public record ShopSearchResponse(
        String lastBuildDate,
        int total,
        int start,
        int display,
        List<ShopSearchItem> items
) {}