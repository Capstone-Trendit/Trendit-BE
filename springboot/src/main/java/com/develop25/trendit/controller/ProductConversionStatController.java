package com.develop25.trendit.controller;

import com.develop25.trendit.domain.ProductConversionStat;
import com.develop25.trendit.service.ProductConversionStatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class ProductConversionStatController {

    private final ProductConversionStatService productConversionStatService;

    @Operation(
            summary = "가장 최근 구매전환율 조회",
            description = "주어진 productId에 대해 가장 최근 window의 구매전환율을 반환합니다."
    )
    @GetMapping("/conversion-rate/{productId}")
    public ResponseEntity<ProductConversionStat> getLatestConversionStat(@PathVariable Long productId) {
        return ResponseEntity.ok(productConversionStatService.getLatestConversionStat(productId));
    }
}
