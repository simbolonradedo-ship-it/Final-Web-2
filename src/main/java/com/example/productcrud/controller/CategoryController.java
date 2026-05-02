package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Helper method untuk mendapatkan user yang sedang login
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUser();
        }
        return null;
    }

    /**
     * List all categories for current user
     */
    @GetMapping
    public String listCategories(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("categories", categoryService.findAllByUser(currentUser));
        model.addAttribute("pageTitle", "Kategori");
        return "category/list";
    }

    /**
     * Show create category form
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Kategori baru");
        return "category/form";
    }

    /**
     * Show edit category form
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        return categoryService.findByIdAndUser(id, currentUser)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("pageTitle", "Edit kategori");
                    return "category/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Category tidak ditemukan atau bukan milik Anda");
                    return "redirect:/categories";
                });
    }

    /**
     * Save category (create or update)
     */
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute Category category, 
                               RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            categoryService.save(category, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", 
                category.getId() != null ? "Category berhasil diperbarui" : "Category berhasil ditambahkan");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/categories/new";
        }

        return "redirect:/categories";
    }

    /**
     * Delete category
     */
    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            categoryService.deleteByIdAndUser(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Category berhasil dihapus");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/categories";
    }
}
