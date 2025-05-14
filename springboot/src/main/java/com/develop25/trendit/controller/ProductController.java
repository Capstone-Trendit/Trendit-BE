package com.develop25.trendit.controller;
import com.develop25.trendit.domain.Image;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.dto.ProductDTO;
import com.develop25.trendit.repository.ProductImageRepository;
import com.develop25.trendit.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
@Slf4j
@RestController
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageRepository productImageRepository;
//    @Autowired
//    private ImageTagService imageTagService;

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
    @PostMapping("/api/productCreate/image")
    public Image createProduct(@RequestParam("file") MultipartFile file) throws IOException {
        Image image = new Image();
        image.setImage(file.getBytes()); // 이미지 데이터를 byte[]로 저장

        return productImageRepository.save(image);
    }
}
