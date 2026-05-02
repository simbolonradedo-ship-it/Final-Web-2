package com.example.productcrud.service;

import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.repository.ProductSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findFilteredPage(String search, Long minPrice, Long maxPrice,
                                          Integer minStock, Integer maxStock, Pageable pageable) {
        String namePart = (search != null && !search.isBlank()) ? search.trim() : null;
        return productRepository.findAll(
                ProductSpecifications.filtered(namePart, minPrice, maxPrice, minStock, maxStock),
                pageable);
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}
