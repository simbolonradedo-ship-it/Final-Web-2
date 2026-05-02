package com.example.productcrud.controller.api;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import com.example.productcrud.service.CategoryService;
import com.example.productcrud.service.CustomUserDetails;
import com.example.productcrud.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product Management APIs")
public class ProductApiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ProductApiController(ProductService productService, CategoryService categoryService, 
                               CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) auth.getPrincipal()).getUser();
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve paginated list of products for current user")
    public ResponseEntity<Map<String, Object>> getProducts(
            @Parameter(description = "Search keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Category ID filter") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage;
        
        Category category = null;
        if (categoryId != null) {
            category = categoryRepository.findById(categoryId).orElse(null);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productService.searchAndFilter(currentUser, keyword.trim(), category, pageable);
        } else if (category != null) {
            productPage = productService.findByCategory(currentUser, category, pageable);
        } else {
            productPage = productService.findAllByUser(currentUser, pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", productPage.getContent());
        response.put("totalElements", productPage.getTotalElements());
        response.put("totalPages", productPage.getTotalPages());
        response.put("currentPage", productPage.getNumber());
        response.put("size", productPage.getSize());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a single product by its ID")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        
        return productService.findByIdAndUser(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> payload) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Product product = new Product();
            product.setName((String) payload.get("name"));
            product.setDescription((String) payload.get("description"));
            product.setPrice(Long.valueOf(payload.get("price").toString()));
            product.setStock(Integer.valueOf(payload.get("stock").toString()));
            product.setActive(Boolean.parseBoolean(payload.get("active").toString()));
            product.setCreatedAt(LocalDate.now());
            product.setCreatedBy(currentUser.getUsername());
            product.setUpdatedBy(currentUser.getUsername());

            Long categoryId = Long.valueOf(payload.get("categoryId").toString());
            Category category = categoryService.findByIdAndUser(categoryId, currentUser)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            product.setCategory(category);

            Product saved = productService.save(product, currentUser);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        return productService.findByIdAndUser(id, currentUser)
                .map(product -> {
                    product.setName((String) payload.get("name"));
                    product.setDescription((String) payload.get("description"));
                    product.setPrice(Long.valueOf(payload.get("price").toString()));
                    product.setStock(Integer.valueOf(payload.get("stock").toString()));
                    product.setActive(Boolean.parseBoolean(payload.get("active").toString()));
                    product.setUpdatedBy(currentUser.getUsername());

                    Long categoryId = Long.valueOf(payload.get("categoryId").toString());
                    Category category = categoryService.findByIdAndUser(categoryId, currentUser)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                    product.setCategory(category);

                    Product saved = productService.save(product, currentUser);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product by ID")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            productService.deleteByIdAndUser(id, currentUser);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}