package com.example.productcrud.config;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, 
                                       CategoryRepository categoryRepository,
                                       ProductRepository productRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            seedIfDatabaseEmpty(userRepository, categoryRepository, productRepository, passwordEncoder);
        };
    }

    @Transactional
    protected void seedIfDatabaseEmpty(UserRepository userRepository,
                                       CategoryRepository categoryRepository,
                                       ProductRepository productRepository,
                                       PasswordEncoder passwordEncoder) {
        if (userRepository.count() > 0) {
            System.out.println("Database sudah berisi pengguna dari run sebelumnya. Melewati inisialisasi data awal.");
            return;
        }

        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setFullName("Admin User");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEnabled(true);
        userRepository.save(admin);

        String[] defaultCategories = {"Elektronik", "Buku", "Makanan", "Pakaian"};
        Map<String, Category> categoryMap = new HashMap<>();
        Arrays.stream(defaultCategories).forEach(name -> {
            Category cat = new Category();
            cat.setName(name);
            cat.setDescription("Default category: " + name);
            cat.setUser(admin);
            categoryRepository.save(cat);
            categoryMap.put(name, cat);
        });

        Product p1 = new Product();
        p1.setName("Laptop Ultrabook 14\"");
        p1.setCategory(categoryMap.get("Elektronik"));
        p1.setPrice(18500000);
        p1.setStock(12);
        p1.setDescription("Ringan, kencang, baterai awet. Cocok untuk kerja & kuliah.");
        p1.setActive(true);
        p1.setCreatedAt(LocalDate.now());
        p1.setCreatedBy(admin.getUsername());
        p1.setUpdatedBy(admin.getUsername());

        Product p2 = new Product();
        p2.setName("Buku Clean Code");
        p2.setCategory(categoryMap.get("Buku"));
        p2.setPrice(175000);
        p2.setStock(40);
        p2.setDescription("Buku klasik untuk menulis kode yang rapi dan maintainable.");
        p2.setActive(true);
        p2.setCreatedAt(LocalDate.now());
        p2.setCreatedBy(admin.getUsername());
        p2.setUpdatedBy(admin.getUsername());

        Product p3 = new Product();
        p3.setName("Snack Granola 250g");
        p3.setCategory(categoryMap.get("Makanan"));
        p3.setPrice(45000);
        p3.setStock(100);
        p3.setDescription("Cemilan sehat dengan rasa madu dan kacang.");
        p3.setActive(true);
        p3.setCreatedAt(LocalDate.now());
        p3.setCreatedBy(admin.getUsername());
        p3.setUpdatedBy(admin.getUsername());

        Product p4 = new Product();
        p4.setName("Hoodie Oversize Premium");
        p4.setCategory(categoryMap.get("Pakaian"));
        p4.setPrice(289000);
        p4.setStock(25);
        p4.setDescription("Bahan tebal, lembut, dan nyaman dipakai sehari-hari.");
        p4.setActive(true);
        p4.setCreatedAt(LocalDate.now());
        p4.setCreatedBy(admin.getUsername());
        p4.setUpdatedBy(admin.getUsername());

        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4));

        System.out.println("Default admin, categories, & products created.");
    }
}