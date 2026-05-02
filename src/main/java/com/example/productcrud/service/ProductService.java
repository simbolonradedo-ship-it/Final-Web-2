package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> findAllByUser(User user, Pageable pageable) {
        return productRepository.findAllByCategoryUser(user, pageable);
    }
    
    public List<Product> findAllByUser(User user) {
        return productRepository.findAllByCategoryUser(user);
    }

    public Page<Product> searchAndFilter(User user, String keyword, Category category, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return productRepository.searchAndFilter(user, searchKeyword, category, pageable);
    }

    public Page<Product> searchByKeyword(User user, String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAllByUser(user, pageable);
        }
        return productRepository.searchByKeyword(user, keyword.trim(), pageable);
    }

    public Page<Product> findByCategory(User user, Category category, Pageable pageable) {
        // Pastikan category tidak null agar query aman (meskipun controller sudah dicek)
        if (category == null) {
            // Jika null, fallback ke cari semua saja
            return findAllByUser(user, pageable);
        }
        return productRepository.findAllByCategoryUserAndCategory(user, category, pageable);
    }

    public Optional<Product> findByIdAndUser(Long id, User user) {
        return productRepository.findById(id)
                .filter(p -> p.getCategory() != null && 
                        p.getCategory().getUser() != null &&
                        p.getCategory().getUser().getId().equals(user.getId()));
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product, User user) {
        if (product.getCategory() != null) {
            if (!product.getCategory().getUser().getId().equals(user.getId())) {
                throw new IllegalArgumentException("Category bukan milik Anda");
            }
        }
        
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    public void deleteByIdAndUser(Long id, User user) {
        Optional<Product> productOpt = findByIdAndUser(id, user);
        if (productOpt.isPresent()) {
            productRepository.delete(productOpt.get());
        } else {
            throw new IllegalArgumentException("Product tidak ditemukan atau bukan milik Anda");
        }
    }

    public long getTotalProducts(User user) {
        return productRepository.countByCategoryUser(user);
    }

    public long getActiveProducts(User user) {
        return productRepository.countByCategoryUserAndActiveTrue(user);
    }

    public long getInactiveProducts(User user) {
        return productRepository.countByCategoryUserAndActiveFalse(user);
    }

    public Long getTotalStock(User user) {
        Long result = productRepository.sumStockByCategoryUser(user);
        return result != null ? result : 0L;
    }

    public Long getTotalValue(User user) {
        Long result = productRepository.sumValueByCategoryUser(user);
        return result != null ? result : 0L;
    }
}