package com.develop25.trendit.controller;

import com.develop25.trendit.domain.TopSearchStat;
import com.develop25.trendit.service.TopSearchStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class TopSearchStatController {

    private final TopSearchStatService topSearchStatService;

    @GetMapping("/top-search")
    public List<TopSearchStat> getLatestTopSearchStats() {
        return topSearchStatService.getLatestTop5();
    }
}