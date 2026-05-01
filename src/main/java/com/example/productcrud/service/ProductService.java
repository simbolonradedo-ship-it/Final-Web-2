package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
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

    public List<Product> findActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    public List<Product> findInactiveProducts() {
        return productRepository.findByActiveFalse();
    }

    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public List<Product> findByCreatedAt(LocalDate date) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCreatedAt().equals(date))
                .toList();
    }
}
