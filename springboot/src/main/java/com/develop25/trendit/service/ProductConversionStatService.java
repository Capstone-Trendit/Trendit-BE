package com.develop25.trendit.service;

import com.develop25.trendit.domain.ProductConversionStat;
import com.develop25.trendit.repository.ProductConversionStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductConversionStatService {

    private final ProductConversionStatRepository productConversionStatRepository;

    public ProductConversionStat getLatestConversionStat(Long productId) {
        return productConversionStatRepository.findLatestByProductId(productId)
                .orElseThrow(() -> new RuntimeException("해당 상품의 전환율 데이터가 없습니다."));
    }
}

