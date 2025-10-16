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
import com.develop25.trendit.repository.UserRepository;
import com.develop25.trendit.service.ImageTagService;
import com.develop25.trendit.service.ProductService;
import com.develop25.trendit.service.UtilTagService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
    @Autowired
    private ImageTagService imageTagService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UtilTagService utilTagService;

    private final ProductService productService;

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
        List<String> tags = request.getTags();
        //String userId = request.getUserId();
        //String userPassword = request.getUserPassword();

        //유저 id로 유저 객체 조회
//        User user = userRepository.findByUserIdAndPassword(userId, userPassword)
//                .orElseThrow(() -> new IllegalArgumentException("해당 userId를 가진 사용자가 존재하지 않습니다: " + userId));


        // ✅ Step 1: 상품 정보 저장
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCount(count);
        //product.setUser(user);
        product = productRepository.save(product);

        // ✅ Step 2: 이미지 저장
        Image image = new Image();
        image.setProduct(product); // 연관관계 설정
        image.setImage(file.getBytes());
        productImageRepository.save(image);

        // ✅ Step 3: 태그 저장
        //ObjectMapper mapper = new ObjectMapper();
        //List<String> tags = mapper.readValue(tagsJson, new TypeReference<List<String>>() {});

        for (String tagStr : tags) {
            Tag tag = new Tag();
            tag.setName(tagStr);
            tagRepository.save(tag); // 먼저 tag를 저장해야 ID가 생성됨

            product.getTags().add(tag); // ✅ 관계 설정은 product 쪽에서
        }
        productRepository.save(product); // ✅ 연관관계 반영

        utilTagService.saveTags(name, tags);

        return ResponseEntity.ok("상품 등록 완료");
    }

    @Operation(summary = "해당 유저가 등록한 모든 상품 조회")
    @GetMapping("/product/{userId}")
    public List<Product> getProductsByUser(@PathVariable String userId) {
        return productService.getProductsByUserId(userId);
    }

    @Operation(summary = "해당 상품 정보 수정")
    @PutMapping("/product/update/{productId}")
    public Product updateProduct(@PathVariable Long productId, @org.springframework.web.bind.annotation.RequestBody ProductDTO dto) {
        System.out.println("📥 요청 DTO: " + dto);
        return productService.updateProduct(productId, dto);
    }
}