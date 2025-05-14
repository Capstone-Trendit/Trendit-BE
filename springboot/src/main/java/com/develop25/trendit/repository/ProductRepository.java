package com.develop25.trendit.repository;

import com.develop25.trendit.domain.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Override
    ArrayList<Product> findAll();
}