package com.develop25.trendit.controller;

import com.develop25.trendit.domain.ProductConversionStat;
import com.develop25.trendit.service.ProductConversionStatService;
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

    @GetMapping("/conversion-rate/{productId}")
    public ResponseEntity<ProductConversionStat> getLatestConversionStat(@PathVariable Long productId) {
        return ResponseEntity.ok(productConversionStatService.getLatestConversionStat(productId));
    }
}
