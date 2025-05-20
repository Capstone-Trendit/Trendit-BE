package com.develop25.trendit.controller;

import com.develop25.trendit.domain.TopProductStat;
import com.develop25.trendit.service.TopProductStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class TopProductStatController {
    private final TopProductStatService topProductStatService;

    @GetMapping("/top-purchase")
    public ResponseEntity<List<TopProductStat>> getTop5() {
        return ResponseEntity.ok(topProductStatService.getLatestTop5());
    }
}
