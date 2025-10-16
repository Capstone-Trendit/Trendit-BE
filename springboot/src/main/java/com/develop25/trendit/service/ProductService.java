package com.develop25.trendit.service;

import com.develop25.trendit.domain.Product;
import com.develop25.trendit.dto.ProductDTO;
import com.develop25.trendit.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getProductsByUserId(String userId) {
        return productRepository.findByUser_UserId(userId);
    }
    @Transactional
    public Product updateProduct(Long productId, ProductDTO dto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품을 찾을 수 없습니다. id=" + productId));

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getCount() != null) {
            product.setCount(dto.getCount());
        }
        return productRepository.save(product);
    }
}
