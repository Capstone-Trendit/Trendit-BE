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
import com.develop25.trendit.service.*;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UtilTagService utilTagService;
    @Autowired
    private SbertService sbertService;
    @Autowired
    private CategoryTagService categoryTagService ;
    @Autowired
    private TagSimilarityService tagSimilarityService ;


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
        List<String> tags = request.getTags(); //여기까지는 이미지를 통해 얻은 태그임
        //String userId = request.getUserId();
        //String userPassword = request.getUserPassword();

        //네이버 쇼핑에서 상품명에 대한 카테고리 추출
        CategoryTagService.CategoryTagResult naverSearch = categoryTagService.categoryTagForProductName(name);
        List<String> naverSearchTag = naverSearch.tag();

        //이미지 태그와 네이버 쇼핑 대표 카테고리와 유사도 계산해서 설정한 임계값(threshold) 이상이면 네이버 쇼핑의 태그로 대체하여
        //최종 태그 생성
        List<String> finalTags = tagSimilarityService.replaceIfSimilar(tags, naverSearchTag, 0.8);

        //유저 id로 유저 객체 조회
//        User user = userRepository.findByUserIdAndPassword(userId, userPassword)
//                .orElseThrow(() -> new IllegalArgumentException("해당 userId를 가진 사용자가 존재하지 않습니다: " + userId));


        //  Step 1: 상품 정보 저장
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCount(count);

//        //현 상품 임베딩 값 계산
//        product.setEmbedding(sbertService.embed(name));
//
//        //모든 상품들의 임베딩 값 가져오고 유사도 계산하여 tag_product_name 정하기
//        //문제점1. 마지막으로 0.8이상인 태그를 가져옴
//        //문제점2. 모든 상품 비교라 계산 소요가 큼
//        List<ProductEmbeddingView> all = productRepository.findAllEmbeddings();
//        ProductEmbeddingView best = null;
//        double bestSim = -1.0;
//        for (ProductEmbeddingView v : all) {
//            float[] e = v.getEmbedding();
//            if (e == null || e.length == 0) continue;
//
//            double sim = sbertService.cosine(product.getEmbedding(), e);
//            if (!Double.isNaN(sim) && sim > bestSim) {
//                bestSim = sim; //가장 비슷한 유사도
//                best = v; //가장 비슷한 상품
//            }
//        }
//        if(best != null && bestSim >= 0.8){ //유사도가 0.8이상인 경우
//            product.setTagProductName(best.getName());
//        }
//        else{
//            product.setTagProductName(product.getName());
//        }
//

        //product.setUser(user);
        product = productRepository.save(product);

        //  Step 2: 이미지 저장
        Image image = new Image();
        image.setProduct(product); // 연관관계 설정
        image.setImage(file.getBytes());
        productImageRepository.save(image);

        //  Step 3: 태그 저장
        //ObjectMapper mapper = new ObjectMapper();
        //List<String> tags = mapper.readValue(tagsJson, new TypeReference<List<String>>() {});

        for (String tagStr : finalTags) {
            Tag tag = new Tag();
            tag.setName(tagStr);
            tagRepository.save(tag); // 먼저 tag를 저장해야 ID가 생성됨

            product.getTags().add(tag); //  관계 설정은 product 쪽에서
        }
        productRepository.save(product); //  연관관계 반영

        utilTagService.saveTags(name, finalTags);

        return ResponseEntity.ok("상품 등록 완료");
    }
}
