package com.develop25.trendit.repository;

import com.develop25.trendit.domain.TimeSlotSalesStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotSalesStatRepository extends JpaRepository<TimeSlotSalesStat, Long> {

    // 특정 상품의 가장 최근 통계일 조회
    @Query("SELECT MAX(t.statDate) FROM TimeSlotSalesStat t WHERE t.product.productId = :productId")
    LocalDate findLatestStatDateByProductId(@Param("productId") Long productId);

    // 특정 상품, 날짜 기준으로 시간대별 판매 통계 조회
    List<TimeSlotSalesStat> findByProduct_ProductIdAndStatDateOrderByHourOfDay(Long productId, LocalDate statDate);
}
