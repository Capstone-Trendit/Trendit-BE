package com.develop25.trendit.repository;

import com.develop25.trendit.domain.TopProductStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopProductStatRepository extends JpaRepository<TopProductStat, Long> {
    @Query("SELECT t FROM TopProductStat t WHERE t.windowEnd = " +
            "(SELECT MAX(t2.windowEnd) FROM TopProductStat t2) ORDER BY t.ranking ASC")
    List<TopProductStat> findLatestTop5();
}
