package com.develop25.trendit.service;

import com.develop25.trendit.domain.RepeatPurchaseStat;
import com.develop25.trendit.repository.RepeatPurchaseStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepeatPurchaseStatService {

    private final RepeatPurchaseStatRepository repeatPurchaseStatRepository;

    public RepeatPurchaseStat getLatestRepeatPurchaseStat(Long productId) {
        return repeatPurchaseStatRepository.findLatestByProductId(productId)
                .orElseThrow(() -> new RuntimeException("해당 상품에 대한 재구매율 데이터가 없습니다."));
    }
}