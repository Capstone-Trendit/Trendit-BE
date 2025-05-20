package com.develop25.trendit.controller;

import com.develop25.trendit.domain.TimeSlotSalesStat;
import com.develop25.trendit.dto.TimeSlotSalesStatDto;
import com.develop25.trendit.service.TimeSlotSalesStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class TimeSlotSalesStatController {

    private final TimeSlotSalesStatService statService;

    @GetMapping("/{productId}/hourly-sales")
    public ResponseEntity<List<TimeSlotSalesStatDto>> getLatestHourlyStats(@PathVariable Long productId) {
        return ResponseEntity.ok(statService.getLatestHourlyStatsByProductId(productId));
    }
}