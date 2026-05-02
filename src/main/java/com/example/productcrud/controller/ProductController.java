package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.UserRepository;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.CustomUserDetails;
import com.example.productcrud.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    public ProductController(ProductService productService, CategoryService categoryService, UserRepository userRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUser();
        }
        return null;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(Model model,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) Long categoryId,
                               @RequestParam(defaultValue = "false") boolean catalog,
                               @RequestParam(defaultValue = "0") int page) {

        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";

        User adminUser = userRepository.findByUsername("admin").orElse(null);
        User targetUser = (catalog && adminUser != null) ? adminUser : currentUser;

        Pageable pageable = PageRequest.of(page, 10);
        List<Category> filterCategories = categoryService.findAllByUser(targetUser);
        Category category = null;
        if (categoryId != null) {
            category = filterCategories.stream().filter(c -> c.getId().equals(categoryId)).findFirst().orElse(null);
        }

        Page<Product> productPage;
        boolean hasFilter = (keyword != null && !keyword.trim().isEmpty()) || category != null;

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchAndFilter(targetUser, keyword.trim(), category, pageable);
        } else if (category != null) {
            productPage = productService.findByCategory(targetUser, category, pageable);
            if (productPage.isEmpty()) {
                model.addAttribute("emptyCategoryMessage", "Tidak ada barang di kategori ini.");
            }
        } else {
            productPage = productService.findAllByUser(targetUser, pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("productPage", productPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", filterCategories);
        model.addAttribute("isCatalogView", catalog);
        model.addAttribute("isMyProductView", !catalog);
        model.addAttribute("pageTitle", catalog ? "Katalog Produk" : "Produk Saya");

        return "product/list";
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAllByUser(currentUser));
        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, Model model, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";

        try {
            if (product.getCategory() == null || product.getCategory().getId() == null) {
                throw new IllegalArgumentException("Kategori harus dipilih");
            }
            Category actualCategory = categoryService.findByIdAndUser(product.getCategory().getId(), currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Kategori tidak valid"));

            product.setCategory(actualCategory);
            if (product.getId() == null) product.setCreatedBy(currentUser.getUsername());
            product.setUpdatedBy(currentUser.getUsername());
            product.setCreatedAt(LocalDate.now());

            productService.save(product, currentUser);
            ra.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            model.addAttribute("categories", categoryService.findAllByUser(currentUser));
            return "product/form";
        }
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        return productService.findByIdAndUser(id, currentUser)
                .map(p -> {
                    model.addAttribute("product", p);
                    model.addAttribute("categories", categoryService.findAllByUser(currentUser));
                    return "product/form";
                }).orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Produk tidak ditemukan");
                    return "redirect:/products";
                });
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        try {
            productService.deleteByIdAndUser(id, currentUser);
            ra.addFlashAttribute("successMessage", "Produk berhasil dihapus");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/products";
    }

    @GetMapping("/products/{id}")
    public String detailProduct(@PathVariable Long id, Model model, RedirectAttributes ra) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";
        return productService.findByIdAndUser(id, currentUser)
                .map(p -> {
                    model.addAttribute("product", p);
                    return "product/detail";
                }).orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage", "Produk tidak ditemukan");
                    return "redirect:/products";
                });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) return "redirect:/auth/login";

        model.addAttribute("totalProducts", productService.getTotalProducts(currentUser));
        model.addAttribute("activeProducts", productService.getActiveProducts(currentUser));
        model.addAttribute("inactiveProducts", productService.getInactiveProducts(currentUser));
        model.addAttribute("totalStock", productService.getTotalStock(currentUser));
        model.addAttribute("totalValue", productService.getTotalValue(currentUser));
        model.addAttribute("pageTitle", "Dashboard");

        return "dashboard";
    }
}