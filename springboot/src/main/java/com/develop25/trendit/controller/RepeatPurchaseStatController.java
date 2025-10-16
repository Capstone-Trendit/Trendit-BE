package com.develop25.trendit.controller;

import com.develop25.trendit.domain.RepeatPurchaseStat;
import com.develop25.trendit.service.RepeatPurchaseStatService;
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
public class RepeatPurchaseStatController {

    private final RepeatPurchaseStatService repeatPurchaseStatService;

    @Operation(
            summary = "가장 최근 재구매율 조회",
            description = "주어진 productId에 대해 가장 최근 window의 재구매율을 반환합니다."
    )
    @GetMapping("/repeat-purchase/{productId}")
    public ResponseEntity<RepeatPurchaseStat> getLatestRepeatPurchaseStat(@PathVariable Long productId) {
        return ResponseEntity.ok(repeatPurchaseStatService.getLatestRepeatPurchaseStat(productId));
    }
}
