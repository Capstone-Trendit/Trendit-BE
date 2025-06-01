package com.develop25.trendit.repository;

import com.develop25.trendit.domain.BasicTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicTagRepository extends JpaRepository<BasicTag, Long> {

    @Query("SELECT bt.tag FROM BasicTag bt WHERE bt.productName = :productName")
    List<String> findTagsByProductName(@Param("productName") String productName);
}
