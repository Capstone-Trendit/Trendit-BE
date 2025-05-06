package com.develop25.trendit.service;

import com.develop25.trendit.domain.TopProductStat;
import com.develop25.trendit.repository.TopProductStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopProductStatService {
    private final TopProductStatRepository topProductStatRepository;

    public List<TopProductStat> getLatestTop5() {
        return topProductStatRepository.findLatestTop5();
    }
}
