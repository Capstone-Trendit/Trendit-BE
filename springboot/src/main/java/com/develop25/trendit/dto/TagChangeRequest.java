package com.develop25.trendit.dto;

import java.util.List;

public class TagChangeRequest {
    private String name;           // 상품명
    private List<String> tags;     // 태그 목록

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}