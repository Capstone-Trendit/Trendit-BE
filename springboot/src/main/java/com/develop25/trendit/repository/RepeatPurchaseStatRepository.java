package com.develop25.trendit.repository;

import com.develop25.trendit.domain.ProductWindowKey;
import com.develop25.trendit.domain.RepeatPurchaseStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepeatPurchaseStatRepository extends JpaRepository<RepeatPurchaseStat, ProductWindowKey> {

    @Query("SELECT r FROM RepeatPurchaseStat r WHERE r.id.productId = :productId " +
            "AND r.id.windowStart = (SELECT MAX(r2.id.windowStart) FROM RepeatPurchaseStat r2 WHERE r2.id.productId = :productId)")
    Optional<RepeatPurchaseStat> findLatestByProductId(@Param("productId") Long productId);
}