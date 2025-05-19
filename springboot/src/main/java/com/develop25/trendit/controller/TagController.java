package com.develop25.trendit.controller;

import com.develop25.trendit.domain.Image;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.domain.Tag;
import com.develop25.trendit.dto.ImageUploadRequest;
import com.develop25.trendit.repository.ProductImageRepository;
import com.develop25.trendit.repository.ProductRepository;
import com.develop25.trendit.repository.TagRepository;
import com.develop25.trendit.service.ImageTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tag")

public class TagController {

    @Autowired
    private ProductImageRepository imageRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private ImageTagService tagService;

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private  ImageUploadRequest imageUploadRequest;

    @Operation(
            summary = "태그 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = ImageUploadRequest.class)
                    )
            )
    )
    @PostMapping(value = "/generate-tags", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> generateTags(@ModelAttribute ImageUploadRequest request) throws IOException {

        MultipartFile file = request.getFile();
        byte[] imageBytes = file.getBytes();
        List<String> tags = tagService.generateTags(imageBytes);

        return ResponseEntity.ok(tags);
    }


    @PostMapping("generate-tags/{productId}")
    public ResponseEntity<?> generateTags(@PathVariable Long productId) throws IOException {

        // 1. 상품 엔티티 조회 (tag 연결을 위해 필요)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        // 2. 이미지 조회
        Image image = imageRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("이미지 없음"));

        // 3. 태그 생성
        List<String> tags = tagService.generateTags(image.getImage());

        // 4. 태그 엔티티 생성 및 저장 + 연관관계 설정
        for (String tagStr : tags) {
            Tag tag = new Tag();
            tag.setName(tagStr);
            tagRepository.save(tag); // tag 먼저 저장 (ID 생겨야 product_tag에 들어감)

            product.getTags().add(tag); // ✅ 다대다 관계 연결
        }

        // 5. 연관관계 저장 (product_tag 테이블 반영)
        productRepository.save(product);

        return ResponseEntity.ok(tags);
    }


    //Get
    @GetMapping("{productId}")
    public List<Tag> getTagsByProductId(@PathVariable Long productId) {
        return tagRepository.findByProducts_ProductId(productId);
    }


}
