package com.example.productcrud.config;

import com.example.productcrud.model.Category;
import com.example.productcrud.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void init() {
        if (categoryRepository.count() == 0) {
            Category elektronik = new Category("Elektronik", "Produk elektronik terkini");
            Category buku = new Category("Buku", "Buku bacaan dan referensi");
            Category makanan = new Category("Makanan", "Makanan dan minuman");
            Category pakaian = new Category("Pakaian", "Pakaian dan aksesoris");

            categoryRepository.saveAll(Arrays.asList(elektronik, buku, makanan, pakaian));
            System.out.println("Database initialized with default categories");
        }
    }
}
