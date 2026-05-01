package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    long countByStockGreaterThan(int stock);
    
    long countByStock(int stock);
    
    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Product p")
    Integer sumStock();
    
    @Query("SELECT COALESCE(SUM(p.price * p.stock), 0) FROM Product p")
    Long sumPriceTimesStock();
}
