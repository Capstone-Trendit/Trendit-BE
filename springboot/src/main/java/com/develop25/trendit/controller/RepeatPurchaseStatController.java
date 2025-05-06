package com.develop25.trendit.controller;

import com.develop25.trendit.domain.RepeatPurchaseStat;
import com.develop25.trendit.service.RepeatPurchaseStatService;
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

    @GetMapping("/repeat-purchase/{productId}")
    public ResponseEntity<RepeatPurchaseStat> getLatestRepeatPurchaseStat(@PathVariable Long productId) {
        return ResponseEntity.ok(repeatPurchaseStatService.getLatestRepeatPurchaseStat(productId));
    }
}
