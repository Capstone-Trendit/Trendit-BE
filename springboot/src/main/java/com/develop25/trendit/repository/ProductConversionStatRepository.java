package com.develop25.trendit.repository;

import com.develop25.trendit.domain.ProductConversionStat;
import com.develop25.trendit.domain.ProductWindowKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductConversionStatRepository extends JpaRepository<ProductConversionStat, ProductWindowKey> {

    @Query("SELECT p FROM ProductConversionStat p WHERE p.id.productId = :productId " +
            "AND p.id.windowStart = (SELECT MAX(p2.id.windowStart) FROM ProductConversionStat p2 WHERE p2.id.productId = :productId)")
    Optional<ProductConversionStat> findLatestByProductId(@Param("productId") Long productId);
}
