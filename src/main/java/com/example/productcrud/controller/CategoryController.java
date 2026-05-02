package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        int pageSize = Math.min(50, Math.max(1, size));
        int pageIndex = Math.max(0, page);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("name").ascending());
        Page<Category> categoryPage = categoryService.findPage(search, pageable);

        if (categoryPage.getTotalPages() > 0 && pageIndex >= categoryPage.getTotalPages()) {
            pageIndex = categoryPage.getTotalPages() - 1;
            pageable = PageRequest.of(pageIndex, pageSize, Sort.by("name").ascending());
            categoryPage = categoryService.findPage(search, pageable);
        }

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("search", search);

        PaginationUtil.addPageWindow(model, categoryPage.getNumber(), categoryPage.getTotalPages());

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

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        categoryService.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil disimpan!");
        return "redirect:/categories";
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

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Kategori berhasil dihapus!");
        return "redirect:/categories";
    }
}
