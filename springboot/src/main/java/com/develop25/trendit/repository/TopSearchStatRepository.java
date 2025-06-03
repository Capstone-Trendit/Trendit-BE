package com.develop25.trendit.repository;

import com.develop25.trendit.domain.TopSearchStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopSearchStatRepository extends JpaRepository<TopSearchStat, Long> {

    @Query("SELECT t FROM TopSearchStat t WHERE t.id.windowStart = (" +
            "SELECT MAX(t2.id.windowStart) FROM TopSearchStat t2) " +
            "ORDER BY t.ranking ASC")
    List<TopSearchStat> findLatestTop5();
}