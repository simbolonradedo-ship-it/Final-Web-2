package com.example.productcrud.repository;

import com.example.productcrud.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> filtered(String namePart, Long minPrice, Long maxPrice,
                                                    Integer minStock, Integer maxStock) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (namePart != null && !namePart.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + namePart.toLowerCase().trim() + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(root.get("price"), maxPrice));
            }
            if (minStock != null) {
                predicates.add(cb.ge(root.get("stock"), minStock));
            }
            if (maxStock != null) {
                predicates.add(cb.le(root.get("stock"), maxStock));
            }
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
