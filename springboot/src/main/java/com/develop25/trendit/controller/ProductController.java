package com.develop25.trendit.controller;
import com.develop25.trendit.domain.Image;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.domain.Tag;
import com.develop25.trendit.domain.User;
import com.develop25.trendit.dto.ProductDTO;
import com.develop25.trendit.dto.ProductRegisterRequest;
import com.develop25.trendit.repository.ProductImageRepository;
import com.develop25.trendit.repository.ProductRepository;
import com.develop25.trendit.repository.TagRepository;
import com.develop25.trendit.service.ImageTagService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Encoding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ImageTagService imageTagService;
    @Autowired
    private TagRepository tagRepository;

    //POST
    //상품 등록 api

    @PostMapping("/api/productCreate")
    public Product create(@org.springframework.web.bind.annotation.RequestBody ProductDTO dto){
        //이 userId는 get으로 받아야 하는 데 일단 임시로 1로 해준것임($$)
        //확신은 못하지만 로그인 정보를 통해서 user테이블에 접근해서 user_id를 가져올 거임
        Long userId = 1L;
        Product product = dto.toEntity();

        return productRepository.save(product);
    }

    //PATCH
    //상품 수정 api
    @PatchMapping("/api/product/{productId}")
    public ResponseEntity<Product> update(@PathVariable Long productId, @org.springframework.web.bind.annotation.RequestBody ProductDTO dto){
        Long userId = 1L;
        //dto 변환
        Product product = dto.toEntity();
        //타겟 조회
        Product target = productRepository.findById(productId).orElse(null);
        //잘못된 요청 처리
        //해당 id가 없거나 수정요청한 id와 db의 id가 다른 경우 상태메세지 400 리턴
        if(target == null || productId != target.getProductId()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        target.patch(product);
        Product updated = productRepository.save(target);

        return ResponseEntity.status(HttpStatus.OK).body(updated);

    }

    //DELETE
    //상품 삭제 api
    @DeleteMapping("/api/product/{productId}")
    public ResponseEntity<Product> delete(@PathVariable Long productId){
        //타겟 조회
        Product target = productRepository.findById(productId).orElse(null);
        //잘못된 요청 처리
        if(target == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        //대상 삭제
        productRepository.delete(target);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();


    }

    //POST
    //이미지 등록 api
    @Operation(summary = "상품 이미지 등록 API")
    @PostMapping("/api/productCreate/image")
    public Image createProduct(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        Image image = new Image();
        image.setImage(file.getBytes());
        return productImageRepository.save(image);
    }

    //swagger의 multipart/form-data 인식을 위한 어노테이션
    @Operation(
            summary = "상품 등록",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "object", implementation = ProductRegisterRequest.class)
                    )
            )
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerProduct(@ModelAttribute ProductRegisterRequest request) throws IOException {

        MultipartFile file = request.getFile();
        String name = request.getName();
        Double price = request.getPrice();
        Long count = request.getCount();
        String tagsJson = request.getTags();
        User user = request.getUser();

        // ✅ Step 1: 상품 정보 저장
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCount(count);
        product.setUser(user);
        product = productRepository.save(product);

        // ✅ Step 2: 이미지 저장
        Image image = new Image();
        image.setProduct(product); // 연관관계 설정
        image.setImage(file.getBytes());
        productImageRepository.save(image);

        // ✅ Step 3: 태그 저장
        ObjectMapper mapper = new ObjectMapper();
        List<String> tags = mapper.readValue(tagsJson, new TypeReference<List<String>>() {});

        for (String tagStr : tags) {
            Tag tag = new Tag();
            tag.setName(tagStr);
            tagRepository.save(tag); // 먼저 tag를 저장해야 ID가 생성됨

            product.getTags().add(tag); // ✅ 관계 설정은 product 쪽에서
        }
        productRepository.save(product); // ✅ 연관관계 반영

        return ResponseEntity.ok("상품 등록 완료");
    }
}
