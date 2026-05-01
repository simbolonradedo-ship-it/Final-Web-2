package com.example.productcrud.config;

import com.example.productcrud.model.Category;
import com.example.productcrud.model.Product;
import com.example.productcrud.repository.CategoryRepository;
import com.example.productcrud.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @PostConstruct
    public void init() {
        // Hanya inisialisasi jika database kosong
        if (categoryRepository.count() == 0) {
            // Buat kategori default
            Category elektronik = new Category("Elektronik", "Produk elektronik terkini");
            Category buku = new Category("Buku", "Buku bacaan dan referensi");
            Category makanan = new Category("Makanan", "Makanan dan minuman");
            Category pakaian = new Category("Pakaian", "Pakaian dan aksesoris");

            categoryRepository.saveAll(Arrays.asList(elektronik, buku, makanan, pakaian));

            // Buat produk default
            Product p1 = new Product();
            p1.setName("Laptop ASUS ROG");
            p1.setCategory(elektronik);
            p1.setPrice(18500000);
            p1.setStock(8);
            p1.setDescription("Laptop gaming ASUS ROG dengan prosesor terbaru dan kartu grafis RTX");
            p1.setActive(true);
            p1.setCreatedAt(LocalDate.now());

            Product p2 = new Product();
            p2.setName("Mouse Logitech MX Master");
            p2.setCategory(elektronik);
            p2.setPrice(1200000);
            p2.setStock(35);
            p2.setDescription("Mouse wireless ergonomis dengan sensor presisi tinggi");
            p2.setActive(true);
            p2.setCreatedAt(LocalDate.now().minusMonths(1));

            Product p3 = new Product();
            p3.setName("Buku Java Programming");
            p3.setCategory(buku);
            p3.setPrice(150000);
            p3.setStock(30);
            p3.setDescription("Buku panduan lengkap pemrograman Java dari dasar hingga mahir");
            p3.setActive(true);
            p3.setCreatedAt(LocalDate.now().minusMonths(2));

            Product p4 = new Product();
            p4.setName("Kopi Arabica Toraja 250g");
            p4.setCategory(makanan);
            p4.setPrice(85000);
            p4.setStock(100);
            p4.setDescription("Kopi arabica premium dari Toraja dengan cita rasa khas");
            p4.setActive(true);
            p4.setCreatedAt(LocalDate.now().minusMonths(3));

            Product p5 = new Product();
            p5.setName("Headphone Sony WH-1000XM5");
            p5.setCategory(elektronik);
            p5.setPrice(4500000);
            p5.setStock(15);
            p5.setDescription("Headphone wireless dengan noise cancelling terbaik di kelasnya");
            p5.setActive(true);
            p5.setCreatedAt(LocalDate.now().minusMonths(4));

            Product p6 = new Product();
            p6.setName("Kemeja Batik Premium");
            p6.setCategory(pakaian);
            p6.setPrice(350000);
            p6.setStock(50);
            p6.setDescription("Kemeja batik premium motif parang dengan bahan katun berkualitas");
            p6.setActive(false);
            p6.setCreatedAt(LocalDate.now().minusMonths(5));

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6));

            System.out.println("Database initialized with default categories and products");
        }
    }
}
