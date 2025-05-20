package com.develop25.trendit.service;

import com.develop25.trendit.domain.TimeSlotSalesStat;
import com.develop25.trendit.dto.TimeSlotSalesStatDto;
import com.develop25.trendit.repository.TimeSlotSalesStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotSalesStatService {
    private final TimeSlotSalesStatRepository statRepository;

    public List<TimeSlotSalesStatDto> getLatestHourlyStatsByProductId(Long productId) {
        LocalDate latestDate = statRepository.findLatestStatDateByProductId(productId);
        if (latestDate == null) {
            return Collections.emptyList();
        }

        List<TimeSlotSalesStat> stats = statRepository.findByProduct_ProductIdAndStatDateOrderByHourOfDay(productId, latestDate);

        return stats.stream()
                .map(stat -> new TimeSlotSalesStatDto(stat.getHourOfDay(), stat.getSalesCount()))
                .collect(Collectors.toList());
    }
}