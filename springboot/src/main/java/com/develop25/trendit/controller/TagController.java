package com.develop25.trendit.controller;

import com.develop25.trendit.domain.Image;
import com.develop25.trendit.domain.Tag;
import com.develop25.trendit.repository.ProductImageRepository;
import com.develop25.trendit.repository.TagRepository;
import com.develop25.trendit.service.ImageTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tag")

public class TagController {

    @Autowired
    private ProductImageRepository imageRepository;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ImageTagService tagService;

    @Value("${openai.api.key}")
    private String openaiApiKey;


    @PostMapping("generate-tags/{productId}")
    public ResponseEntity<?> generateTags(@PathVariable Long productId) throws IOException {
        Image image = imageRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("이미지 없음"));

        List<String> tags = tagService.generateTags(image.getImage());

        List<Tag> tagEntities = tags.stream()
                .map(tag -> {
                    Tag t = new Tag();
                    t.setProductId(productId);
                    t.setTag(tag);
                    return t;
                })
                .collect(Collectors.toList());

        tagRepository.saveAll(tagEntities);
        return ResponseEntity.ok(tagEntities);
    }

    //Get
    @GetMapping("{productId}")
    public List<Tag> getTagsByProductId(@PathVariable Long productId) {
        return tagRepository.findByProductId(productId);
    }


}
