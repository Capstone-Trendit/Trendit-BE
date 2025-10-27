package com.develop25.trendit.controller;

import com.develop25.trendit.domain.Image;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.domain.Tag;
import com.develop25.trendit.dto.ImageUploadRequest;
import com.develop25.trendit.dto.TagChangeRequest;
import com.develop25.trendit.repository.ProductImageRepository;
import com.develop25.trendit.repository.ProductRepository;
import com.develop25.trendit.repository.TagRepository;
import com.develop25.trendit.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
    @Autowired
    private SituationTagService situationTagService;
    @Autowired
    private BasicTagService basicTagService;
    @Autowired
    private CategoryTagService categoryTagService ;
    @Autowired
    private TagSimilarityService tagSimilarityService ;


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
        String productName = request.getProductName();
        // 1. 상품명 기반 태그 먼저 조회
        List<String> basicTags = basicTagService.getTagsByProductName(productName);
        List<String> tags;
        //기본 태그가 없으면 이미지 기반 태그 생성
        if(basicTags.isEmpty()){
            byte[] imageBytes = file.getBytes();
            tags = tagService.generateTags(imageBytes);
        }
        else{
            tags = basicTags;
        }

        // 상품명은 이미지 태그의 첫 번째 요소
        //String productName = tags.isEmpty() ? "" : tags.get(0);
        // 2. 상품명 기반 상황 태그 생성
        //List<String> situationTags = situationTagService.generateAdditionalTags(productName);
        // 3. 태그 통합 (중복 제거)
        Set<String> finalTags = new LinkedHashSet<>(); // 순서 유지
        finalTags.addAll(tags);
        //finalTags.addAll(situationTags);
        return ResponseEntity.ok(new ArrayList<>(finalTags));
    }

    //Get
    @GetMapping("{productId}")
    public List<Tag> getTagsByProductId(@PathVariable Long productId) {
        return tagRepository.findByProducts_ProductId(productId);
    }

    @Operation(description = """
        입력한 태그 목록(tags)을 상품명(name)으로부터 추출한 네이버 쇼핑 대표 카테고리 태그와 비교하여,
        유사도가 임계값(기본 0.7) 이상이면 해당 태그로 치환한 최종 태그 리스트를 반환
        """)
    @PostMapping(path = "/tagChange",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getTagChange(@ModelAttribute TagChangeRequest req) {
        String name = req.getName();
        List<String> tags = req.getTags();
        //네이버 쇼핑에서 상품명에 대한 카테고리 추출
        CategoryTagService.CategoryTagResult naverSearch = categoryTagService.categoryTagForProductName(name);
        System.out.println("naverSearch       = " + naverSearch);
        List<String> naverSearchTag = naverSearch.tag();
        System.out.println("naverSearchTag    = " + naverSearchTag);

        //이미지 태그와 네이버 쇼핑 대표 카테고리와 유사도 계산해서 설정한 임계값(threshold) 이상이면 네이버 쇼핑의 태그로 대체하여
        //최종 태그 생성
        List<String> finalTags = tagSimilarityService.replaceIfSimilar(tags, naverSearchTag, 0.7);
        System.out.println("finalTags(thr=0.7)= " + finalTags);
        return finalTags;
    }




}
