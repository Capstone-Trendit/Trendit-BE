package com.develop25.trendit.repository;

import com.develop25.trendit.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByProductId(Long productId);  // productId로 조회
}
