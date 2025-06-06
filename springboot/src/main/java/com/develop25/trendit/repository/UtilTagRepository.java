package com.develop25.trendit.repository;

import com.develop25.trendit.domain.UtilTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilTagRepository extends JpaRepository<UtilTag, Long> {
    Optional<UtilTag> findByProductNameAndTagName(String productName, String tagName);

    @Query("SELECT u FROM UtilTag u WHERE u.productName = :productName ORDER BY u.count DESC LIMIT 5")
    List<UtilTag> findTop5ByProductNameOrderByCountDesc(String productName);
}
