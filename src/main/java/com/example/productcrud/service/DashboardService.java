package com.example.productcrud.service;

import com.example.productcrud.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductRepository productRepository;

    @Autowired
    public DashboardService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Long getTotalProducts() {
        return productRepository.count();
    }

    public Integer getActiveProducts() {
        return Math.toIntExact(productRepository.countByStockGreaterThan(0));
    }

    public Integer getInactiveProducts() {
        return Math.toIntExact(productRepository.countByStock(0));
    }

    public Integer getTotalStock() {
        return productRepository.sumStock();
    }

    public Long getTotalValue() {
        return productRepository.sumPriceTimesStock();
    }
}
