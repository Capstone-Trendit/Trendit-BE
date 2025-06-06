package com.develop25.trendit.service;
import com.develop25.trendit.domain.BasicTag;
import com.develop25.trendit.domain.UtilTag;
import com.develop25.trendit.repository.BasicTagRepository;
import com.develop25.trendit.repository.TagRepository;
import com.develop25.trendit.repository.UtilTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UtilTagService {

    private final UtilTagRepository utilTagRepository;
    @Autowired
    private BasicTagRepository basicTagRepository;

    @Transactional
    public void saveTags(String productName, List<String> tagNames) {
        for (String tagName : tagNames) {
            utilTagRepository.findByProductNameAndTagName(productName, tagName)
                    .ifPresentOrElse(
                            existingTag -> {
                                existingTag.setCount(existingTag.getCount() + 1);
                                // 변경 감지를 통해 자동 저장됨 (JPA)
                            },
                            () -> {
                                UtilTag newTag = UtilTag.builder()
                                        .productName(productName)
                                        .tagName(tagName)
                                        .count(1)
                                        .build();
                                utilTagRepository.save(newTag);
                            }
                    );
        }
        //  BasicTag 테이블 업데이트
        List<UtilTag> topTags = utilTagRepository.findTop5ByProductNameOrderByCountDesc(productName);
        basicTagRepository.deleteByProductName(productName); // 기존 삭제

        for (UtilTag tag : topTags) {
            BasicTag basicTag = BasicTag.builder()
                    .productName(productName)
                    .tag(tag.getTagName())
                    .build();
            basicTagRepository.save(basicTag);
        }
    }


}
