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

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("errorMessage", "Email sudah digunakan!");
            return "register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setFullName(fullName);
        user.setEnabled(true);

        userRepository.save(user);
        System.out.println("User registered: " + username);

        String[] defaultCategories = {"Elektronik", "Buku", "Makanan", "Pakaian"};
        Category[] savedCategories = new Category[4];
        
        for (int i = 0; i < defaultCategories.length; i++) {
            Category cat = new Category();
            cat.setName(defaultCategories[i]);
            cat.setDescription("Default category: " + defaultCategories[i]);
            cat.setUser(user);
            categoryRepository.save(cat);
            savedCategories[i] = cat;
        }
        System.out.println("Default categories created for user: " + username);

        try {
            Product product1 = new Product();
            product1.setName("Contoh Laptop");
            product1.setDescription("Laptop contoh untuk kategori Elektronik");
            product1.setPrice(10000000);
            product1.setStock(5);
            product1.setCategory(savedCategories[0]);
            product1.setActive(true);
            product1.setCreatedAt(LocalDate.now());
            product1.setCreatedBy(username);
            product1.setUpdatedBy(username);
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("Contoh Buku");
            product2.setDescription("Buku contoh untuk kategori Buku");
            product2.setPrice(50000);
            product2.setStock(20);
            product2.setCategory(savedCategories[1]);
            product2.setActive(true);
            product2.setCreatedAt(LocalDate.now());
            product2.setCreatedBy(username);
            product2.setUpdatedBy(username);
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Contoh Makanan");
            product3.setDescription("Makanan contoh untuk kategori Makanan");
            product3.setPrice(25000);
            product3.setStock(50);
            product3.setCategory(savedCategories[2]);
            product3.setActive(true);
            product3.setCreatedAt(LocalDate.now());
            product3.setCreatedBy(username);
            product3.setUpdatedBy(username);
            productRepository.save(product3);

            Product product4 = new Product();
            product4.setName("Contoh Pakaian");
            product4.setDescription("Pakaian contoh untuk kategori Pakaian");
            product4.setPrice(150000);
            product4.setStock(15);
            product4.setCategory(savedCategories[3]);
            product4.setActive(true);
            product4.setCreatedAt(LocalDate.now());
            product4.setCreatedBy(username);
            product4.setUpdatedBy(username);
            productRepository.save(product4);

            System.out.println("Sample products created for user: " + username);
        } catch (Exception e) {
            System.err.println("Error creating sample products: " + e.getMessage());
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