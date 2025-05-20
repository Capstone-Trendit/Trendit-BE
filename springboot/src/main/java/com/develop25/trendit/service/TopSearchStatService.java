package com.develop25.trendit.service;

import com.develop25.trendit.domain.TopSearchStat;
import com.develop25.trendit.repository.TopSearchStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopSearchStatService {

    private final TopSearchStatRepository repository;

    public List<TopSearchStat> getLatestTop5() {
        return repository.findLatestTop5().stream()
                .limit(5)
                .toList();
    }
}