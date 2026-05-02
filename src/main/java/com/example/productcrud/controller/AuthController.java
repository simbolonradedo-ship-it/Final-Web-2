package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, 
                           CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/auth/login")
    public String loginPage(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau password salah!");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "Anda telah berhasil logout.");
        }
        return "login";
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        return "register";
    }

    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                          @RequestParam String fullName,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam String confirmPassword,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Password dan konfirmasi password tidak cocok!");
            return "register";
        }

        if (password.length() < 6) {
            model.addAttribute("errorMessage", "Password minimal 6 karakter!");
            return "register";
        }

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("errorMessage", "Username sudah digunakan!");
            return "register";
        }

        if (email != null && !email.isBlank() && userRepository.existsByEmail(email.trim())) {
            model.addAttribute("errorMessage", "Email sudah digunakan!");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email != null && !email.isBlank() ? email.trim() : null);
        user.setFullName(fullName);
        user.setEnabled(true);

        userRepository.save(user);
        System.out.println("User registered: " + username);

        // Seed katalog demo untuk SETIAP akun agar bisa CRUD penuh (ownership per user).
        // Target: 5 kategori default + 150 produk demo tersebar merata.
        String[] defaultCategories = {"Elektronik", "Buku", "Makanan", "Pakaian", "Olahraga"};
        Map<String, Category> categoryMap = new HashMap<>();
        for (Category existing : categoryRepository.findByUser(user)) {
            categoryMap.putIfAbsent(existing.getName(), existing);
        }
        for (String catName : defaultCategories) {
            if (!categoryMap.containsKey(catName)) {
                Category cat = new Category();
                cat.setName(catName);
                cat.setDescription("Kategori demo: " + catName);
                cat.setUser(user);
                categoryMap.put(catName, categoryRepository.save(cat));
            }
        }

        final int totalDemoProducts = 150;
        long existingForUser = productRepository.countByCategoryUser(user);
        if (existingForUser < totalDemoProducts) {
            int start = (int) existingForUser + 1;
            LocalDate today = LocalDate.now();
            List<Product> batch = new ArrayList<>(totalDemoProducts - start + 1);
            int nCats = defaultCategories.length;

            List<Product> templateProducts = productRepository.findAll().stream()
                    .sorted(Comparator.comparing(Product::getId))
                    .limit(totalDemoProducts)
                    .toList();

            for (int i = start; i <= totalDemoProducts; i++) {
                Product p = new Product();

                if (!templateProducts.isEmpty()) {
                    Product tpl = templateProducts.get((i - 1) % templateProducts.size());
                    String tplCatName = (tpl.getCategory() != null && tpl.getCategory().getName() != null && !tpl.getCategory().getName().isBlank())
                            ? tpl.getCategory().getName()
                            : defaultCategories[(i - 1) % nCats];
                    Category targetCat = categoryMap.getOrDefault(tplCatName, categoryMap.get(defaultCategories[(i - 1) % nCats]));

                    p.setName(tpl.getName() != null && !tpl.getName().isBlank()
                            ? tpl.getName() + " (demo)"
                            : (tplCatName + " — Item demo #" + i));
                    p.setDescription(tpl.getDescription());
                    p.setPrice(tpl.getPrice());
                    p.setStock(tpl.getStock());
                    p.setActive(tpl.isActive());
                    p.setCategory(targetCat);
                    p.setCreatedAt(tpl.getCreatedAt() != null ? tpl.getCreatedAt() : today);
                    p.setCreatedBy(username);
                    p.setUpdatedBy(username);
                } else {
                    String catName = defaultCategories[(i - 1) % nCats];
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
            System.out.println("Demo catalog seeded for user=" + username + " added=" + batch.size());
        }

        redirectAttributes.addFlashAttribute("successMessage", 
            "Registrasi berhasil! Silakan login dengan akun Anda.");
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/logout")
    public String logout() {
        return "redirect:/auth/login?logout=true";
    }
}