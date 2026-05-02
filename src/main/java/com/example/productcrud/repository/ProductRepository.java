package com.example.productcrud.repository;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByCategoryUser(User user);

    Page<Product> findAllByCategoryUser(User user, Pageable pageable);

    @Query("""
            SELECT p FROM Product p
            WHERE p.category.user = :user
              AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:category IS NULL OR p.category = :category)
            """)
    Page<Product> searchAndFilter(@Param("user") User user,
                                  @Param("keyword") String keyword,
                                  @Param("category") Category category,
                                  Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.user = :user AND LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("user") User user,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    Page<Product> findAllByCategoryUserAndCategory(User user, Category category, Pageable pageable);

    long countByCategoryUser(User user);

    long countByCategoryUserAndActiveTrue(User user);

    long countByCategoryUserAndActiveFalse(User user);

    @Query("SELECT SUM(p.stock) FROM Product p WHERE p.category.user = :user")
    Long sumStockByCategoryUser(@Param("user") User user);

    @Query("SELECT SUM(p.price * p.stock) FROM Product p WHERE p.category.user = :user")
    Long sumValueByCategoryUser(@Param("user") User user);

    long countByCategory_Id(Long categoryId);
}
