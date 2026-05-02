package com.example.productcrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class ProductCrudApplication {

    public static void main(String[] args) {
        loadEnvFile();
        SpringApplication.run(ProductCrudApplication.class, args);
    }

    private static void loadEnvFile() {
        String[] envPaths = {
            ".env",
            "src/main/resources/.env",
            System.getProperty("user.dir") + "/.env"
        };

        for (String path : envPaths) {
            File envFile = new File(path);
            if (envFile.exists()) {
                try (InputStream input = new FileInputStream(envFile)) {
                    Properties props = new Properties();
                    props.load(input);

                    for (String key : props.stringPropertyNames()) {
                        if (System.getProperty(key) == null) {
                            System.setProperty(key, props.getProperty(key));
                        }
                    }
                    System.out.println("Loaded .env file from: " + path);
                    return;
                } catch (Exception e) {
                    System.err.println("Error loading .env file: " + e.getMessage());
                }
            }
        }
        System.out.println("No .env file found, using existing environment variables");
    }
}