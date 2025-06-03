package com.develop25.trendit.service;

import com.develop25.trendit.domain.TopProductStat;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.dto.TopProductStatDto;
import com.develop25.trendit.repository.ProductRepository;
import com.develop25.trendit.repository.TopProductStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopProductStatService {

    private final TopProductStatRepository statRepository;
    private final ProductRepository productRepository;

    public List<TopProductStatDto> getLatestTop5() {
        List<TopProductStat> topStats = statRepository.findLatestTop5()
                .stream()
                .limit(5)
                .toList();

        return topStats.stream()
                .map(stat -> {
                    Long productId = stat.getId().getProductId();
                    String productName = productRepository.findById(productId)
                            .map(Product::getName)
                            .orElse("Unknown Product");
                    return new TopProductStatDto(
                            productId,
                            productName,
                            stat.getSalesVolume(),
                            stat.getRanking()
                    );
                })
                .toList();
    }
}
