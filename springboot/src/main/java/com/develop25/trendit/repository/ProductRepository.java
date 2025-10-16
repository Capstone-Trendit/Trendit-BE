package com.develop25.trendit.repository;

import com.develop25.trendit.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUser_UserId(String userId);

}