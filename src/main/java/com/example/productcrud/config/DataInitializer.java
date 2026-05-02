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
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final String[] DEFAULT_CATEGORIES = {
            "Elektronik", "Buku", "Makanan", "Pakaian", "Olahraga"
    };

    private static final int TOTAL_DEMO_PRODUCTS = 150;

    @Bean
    @Order(2)
    public CommandLineRunner initData(UserRepository userRepository,
                                       CategoryRepository categoryRepository,
                                       ProductRepository productRepository,
                                       PasswordEncoder passwordEncoder,
                                       TransactionTemplate transactionTemplate) {
        return args -> transactionTemplate.executeWithoutResult(status -> {
            repairLegacyNullReferences(userRepository, categoryRepository, productRepository);
            rehashPasswordsIfNotBcrypt(userRepository, passwordEncoder);
            seedIfNeeded(userRepository, categoryRepository, productRepository, passwordEncoder);
        });
    }

    /**
     * Migrasi sekali jalan: password teks polos di DB di-hash BCrypt agar login memakai {@link DaoAuthenticationProvider}.
     */
    private void rehashPasswordsIfNotBcrypt(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        for (User u : userRepository.findAll()) {
            String stored = u.getPassword();
            if (stored == null || stored.isBlank()) {
                continue;
            }
            if (looksLikeBcryptHash(stored)) {
                continue;
            }
            u.setPassword(passwordEncoder.encode(stored));
            userRepository.save(u);
        }
        userRepository.flush();
    }

    private static boolean looksLikeBcryptHash(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }

    private void repairLegacyNullReferences(UserRepository userRepository,
                                            CategoryRepository categoryRepository,
                                            ProductRepository productRepository) {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return;
        }
        User fallbackUser = users.get(0);

        Set<String> takenNames = new HashSet<>();
        for (Category c : categoryRepository.findAll()) {
            if (c.getUser() != null && c.getUser().getId().equals(fallbackUser.getId())) {
                takenNames.add(c.getName());
            }
        }

        for (Category c : categoryRepository.findAll()) {
            if (c.getUser() != null) {
                continue;
            }
            String baseName = c.getName() != null && !c.getName().isBlank() ? c.getName() : "Kategori";
            String uniqueName = nextUniqueCategoryName(baseName, takenNames);
            takenNames.add(uniqueName);
            c.setName(uniqueName);
            c.setUser(fallbackUser);
            categoryRepository.save(c);
        }
        categoryRepository.flush();

        List<Category> allCategories = categoryRepository.findAll();
        if (allCategories.isEmpty()) {
            return;
        }
        Category fallbackCategory = allCategories.get(0);

        for (Product p : productRepository.findAll()) {
            boolean changed = false;
            if (p.getCategory() == null) {
                p.setCategory(fallbackCategory);
                changed = true;
            }
            if (changed) {
                productRepository.save(p);
            }
        }
        productRepository.flush();
    }

    private void seedIfNeeded(UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              ProductRepository productRepository,
                              PasswordEncoder passwordEncoder) {

        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setFullName("Admin User");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("User admin dibuat (password demo: admin123).");
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return;
        }

        int nCats = DEFAULT_CATEGORIES.length;

        // Ambil "template" dari tabel products (kalau ada) untuk dijadikan sumber demo.
        // Karena ownership berbasis category.user, tiap user tetap mendapat COPY produknya sendiri.
        List<Product> templateProducts = productRepository.findAll().stream()
                .sorted(Comparator.comparing(Product::getId))
                .limit(TOTAL_DEMO_PRODUCTS)
                .toList();
        for (User u : users) {
            Map<String, Category> categoryMap = ensureDefaultCategories(u, categoryRepository);

            long existingForUser = productRepository.countByCategoryUser(u);
            if (existingForUser >= TOTAL_DEMO_PRODUCTS) {
                continue;
            }

            int start = (int) existingForUser + 1;
            List<Product> batch = new ArrayList<>(TOTAL_DEMO_PRODUCTS - start + 1);
            LocalDate today = LocalDate.now();
            String username = u.getUsername();

            for (int i = start; i <= TOTAL_DEMO_PRODUCTS; i++) {
                Product p = new Product();

                if (!templateProducts.isEmpty()) {
                    // Clone dari produk yang sudah ada di tabel products
                    Product tpl = templateProducts.get((i - 1) % templateProducts.size());
                    String tplCatName = (tpl.getCategory() != null && tpl.getCategory().getName() != null && !tpl.getCategory().getName().isBlank())
                            ? tpl.getCategory().getName()
                            : DEFAULT_CATEGORIES[(i - 1) % nCats];

                    // Pastikan kategori untuk user ada (kalau template pakai kategori lain, fallback ke default)
                    Category targetCat = categoryMap.getOrDefault(tplCatName, categoryMap.get(DEFAULT_CATEGORIES[(i - 1) % nCats]));

                    p.setName(tpl.getName() != null && !tpl.getName().isBlank()
                            ? tpl.getName() + " (demo)"
                            : (tplCatName + " — Item demo #" + i));
                    p.setDescription(tpl.getDescription());
                    p.setPrice(tpl.getPrice());
                    p.setStock(tpl.getStock());
                    p.setActive(tpl.isActive());
                    p.setCategory(targetCat);

                    // Audit: selalu atas nama pemilik akun ini
                    p.setCreatedAt(tpl.getCreatedAt() != null ? tpl.getCreatedAt() : today);
                    p.setCreatedBy(username);
                    p.setUpdatedBy(username);
                } else {
                    // Fallback: generate demo seperti sebelumnya kalau tabel products masih kosong
                    String catName = DEFAULT_CATEGORIES[(i - 1) % nCats];
                    p.setName(catName + " — Item demo #" + i);
                    p.setDescription("Produk contoh #" + i + " pada kategori " + catName + " untuk pengujian daftar & pagination.");
                    p.setPrice(9_000L + (long) i * 7_500L);
                    p.setStock((i % 120) + 1);
                    p.setCategory(categoryMap.get(catName));
                    p.setActive(true);
                    p.setCreatedAt(today);
                    p.setCreatedBy(username);
                    p.setUpdatedBy(username);
                }
                batch.add(p);
            }

            productRepository.saveAll(batch);
            System.out.println("Demo: user=" + username + " ditambahkan " + batch.size()
                    + " produk (#" + start + "–#" + TOTAL_DEMO_PRODUCTS + "), kategori=" + nCats
                    + ", total user sekarang=" + productRepository.countByCategoryUser(u));
        }
    }

    /**
     * Pastikan ada satu {@link Category} per nama default untuk pemiliknya (tanpa duplikat nama+user).
     */
    private Map<String, Category> ensureDefaultCategories(User owner, CategoryRepository categoryRepository) {
        Map<String, Category> map = new HashMap<>();
        for (Category existing : categoryRepository.findByUser(owner)) {
            map.putIfAbsent(existing.getName(), existing);
        }
        for (String name : DEFAULT_CATEGORIES) {
            if (!map.containsKey(name)) {
                Category cat = new Category();
                cat.setName(name);
                cat.setDescription("Kategori demo: " + name);
                cat.setUser(owner);
                map.put(name, categoryRepository.save(cat));
            }
        }
        return map;
    }

    private static String nextUniqueCategoryName(String base, Set<String> taken) {
        String candidate = base;
        int suffix = 1;
        while (taken.contains(candidate)) {
            candidate = base + " (" + suffix + ")";
            suffix++;
        }
        return candidate;
    }
}
