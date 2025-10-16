package com.develop25.trendit.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class TagSimilarityService {

    private final SbertService sbert;

    public TagSimilarityService(SbertService sbert) {
        this.sbert = sbert;
    }

    //tags vs naverSearchTag 를 임베딩 유사도(코사인)로 비교하여
    //유사도가 임계값 이상이면 tags를 naverSearchTag로 교체해 반환.
    //임계값 기본값: 0.8

    public List<String> replaceIfSimilar(List<String> tags, List<String> naverSearchTag) {
        return replaceIfSimilar(tags, naverSearchTag, 0.8);
    }

    public List<String> replaceIfSimilar(List<String> tags, List<String> naverSearchTag, double threshold) {
        if (isNullOrEmpty(tags) || isNullOrEmpty(naverSearchTag)) {
            return tags; // 비교 불가 → 원본 유지
        }

        // 리스트를 한 문장으로 묶어서 임베딩 (구분자는 자유롭게; " > "가 사람이 보기 좋음)
        String tagsText = String.join(" > ", tags);
        String naverText = String.join(" > ", naverSearchTag);

        float[] e1 = sbert.embed(tagsText);
        float[] e2 = sbert.embed(naverText);
        double sim = SbertService.cosine(e1, e2);

        // 유사도 기준 충족 시 교체
        return (sim >= threshold) ? new ArrayList<>(naverSearchTag) : tags;
    }

    private boolean isNullOrEmpty(List<String> v) {
        if (v == null || v.isEmpty()) return true;
        // 전부 null이면 empty 취급
        return v.stream().filter(Objects::nonNull).map(String::trim).allMatch(String::isEmpty);
    }
}