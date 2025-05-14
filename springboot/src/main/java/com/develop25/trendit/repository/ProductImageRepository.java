package com.develop25.trendit.repository;

import com.develop25.trendit.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository  extends JpaRepository<Image, Long> {
    // 예: 상품 ID로 이미지 리스트 조회
    List<Image> findByProductId(Long productId);
}