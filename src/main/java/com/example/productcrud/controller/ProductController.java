package com.example.productcrud.controller;

import com.example.productcrud.model.Product;
import com.example.productcrud.service.ProductService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/products")
    public String listProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        int pageSize = Math.min(50, Math.max(1, size));
        int pageIndex = Math.max(0, page);

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").ascending());
        Page<Product> productPage = productService.findFilteredPage(
                search, minPrice, maxPrice, minStock, maxStock, pageable);

        if (productPage.getTotalPages() > 0 && pageIndex >= productPage.getTotalPages()) {
            pageIndex = productPage.getTotalPages() - 1;
            pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").ascending());
            productPage = productService.findFilteredPage(
                    search, minPrice, maxPrice, minStock, maxStock, pageable);
        }

        model.addAttribute("productPage", productPage);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("search", search);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minStock", minStock);
        model.addAttribute("maxStock", maxStock);

        PaginationUtil.addPageWindow(model, productPage.getNumber(), productPage.getTotalPages());

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
        model.addAttribute("product", new Product());
        return "product/form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        productService.save(product);
        redirectAttributes.addFlashAttribute("successMessage", "Produk berhasil disimpan!");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        return productService.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
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
