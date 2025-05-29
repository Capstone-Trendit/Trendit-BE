package com.develop25.trendit.service;

import com.develop25.trendit.domain.SituationTag;
import com.develop25.trendit.repository.SituationTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SituationTagService {

    @Autowired
    private SituationTagRepository situationTagRepository;

    public List<String> generateAdditionalTags(String productName) {
        List<SituationTag> situationTags = situationTagRepository.findAll();
        List<String> matchedSituations = new ArrayList<>();

        for (SituationTag tag : situationTags) {
            for (String keyword : tag.getKeywords()) {
                if (productName.contains(keyword)) {
                    matchedSituations.add(tag.getSituation());
                    break; // 한 상황 당 한 번만 추가
                }
            }
        }

        return matchedSituations;
    }
}