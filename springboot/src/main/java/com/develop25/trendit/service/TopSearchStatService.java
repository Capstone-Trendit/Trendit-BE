package com.develop25.trendit.service;

import com.develop25.trendit.dto.TopSearchStatDto;
import com.develop25.trendit.domain.Product;
import com.develop25.trendit.repository.ProductRepository;
import com.develop25.trendit.repository.TopSearchStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopSearchStatService {

    private final TopSearchStatRepository searchStatRepository;
    private final ProductRepository productRepository;

    public List<TopSearchStatDto> getLatestTop5() {
        return searchStatRepository.findLatestTop5().stream()
                .limit(5)
                .map(stat -> {
                    Long productId = stat.getId().getProductId();
                    String productName = productRepository.findById(productId)
                            .map(Product::getName)
                            .orElse("Unknown Product");
                    return new TopSearchStatDto(
                            productId,
                            productName,
                            stat.getSearchVolume(),
                            stat.getRanking()
                    );
                })
                .toList();
    }
}