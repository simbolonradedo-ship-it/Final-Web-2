package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        List<Category> allCategories;
        
        if (search != null && !search.trim().isEmpty()) {
            allCategories = categoryService.findByNameContainingIgnoreCase(search.trim());
        } else {
            allCategories = categoryService.findAll();
        }
        
        // Manual pagination
        int start = page * size;
        int end = Math.min(start + size, allCategories.size());
        List<Category> pagedCategories = start < allCategories.size() 
            ? allCategories.subList(start, end) 
            : java.util.Collections.emptyList();
        
        int totalPages = (int) Math.ceil((double) allCategories.size() / size);
        
        model.addAttribute("categories", pagedCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", allCategories.size());
        model.addAttribute("search", search);
        return "category/list";
    }

    @GetMapping("/{id}")
    public String detailCategory(@PathVariable Long id, Model model) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    return "category/detail";
                })
                .orElse("redirect:/categories");
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        return "category/form";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return categoryService.findById(id)
                .map(category -> {
                    model.addAttribute("category", category);
                    return "category/form";
                })
                .orElse("redirect:/categories");
    }

    @PostMapping
    public String createCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil disimpan!");
        return "redirect:/categories";
    }

    @PutMapping("/{id}")
    public String updateCategory(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        category.setId(id);
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil diperbarui!");
        return "redirect:/categories";
    }

    @DeleteMapping("/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil dihapus!");
        return "redirect:/categories";
    }
}
