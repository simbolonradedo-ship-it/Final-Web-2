package com.example.productcrud.service;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.User;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    public List<Category> findAllByUser(User user) {
        return categoryRepository.findByUser(user);
    }

    public Optional<Category> findByIdAndUser(Long id, User user) {
        return categoryRepository.findById(id)
                .filter(cat -> cat.getUser() != null && cat.getUser().getId().equals(user.getId()));
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category save(Category category, User user) {
        category.setUser(user);

        if (category.getName() != null && category.getName().trim().length() > 0) {
            String normalizedName = category.getName().trim();
            boolean exists = categoryRepository.existsByNameAndUser(normalizedName, user);
            if (exists && (category.getId() == null)) {
                throw new IllegalArgumentException(
                        "Category dengan nama '" + normalizedName + "' sudah ada untuk user Anda"
                );
            }
        }

        return categoryRepository.save(category);
    }

    public void deleteByIdAndUser(Long id, User user) {
        Optional<Category> categoryOpt = findByIdAndUser(id, user);

        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Category tidak ditemukan atau bukan milik Anda");
        }

        long productCount = productRepository.countByCategory_Id(id);
        if (productCount > 0) {
            throw new IllegalStateException(
                    "Tidak dapat menghapus category yang masih digunakan oleh " + productCount + " produk"
            );
        }

        categoryRepository.delete(categoryOpt.get());
    }
}
