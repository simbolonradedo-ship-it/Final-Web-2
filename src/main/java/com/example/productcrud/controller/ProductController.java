package com.example.productcrud.controller;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/products/{id}")
    public String detailProduct(@PathVariable Long id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "product/detail";
                })
                .orElse("redirect:/products");
    }

    @GetMapping("/products/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setCreatedAt(LocalDate.now());
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        // Pastikan Category terload dari DB (bukan detached entity)
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category managedCategory = categoryService.findById(product.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category tidak valid"));
            product.setCategory(managedCategory);
        }
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    model.addAttribute("categories", categoryService.findAll());
                    return "product/form";
                })
                .orElse("redirect:/products");
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil dihapus!");
        return "redirect:/products";
    }
}
