package com.example.productcrud.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.profiles-dir:uploads/profiles}")
    private String profilesDir;

    @PostConstruct
    public void ensureUploadDirExists() throws IOException {
        Path dir = Paths.get(profilesDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Paths.get(profilesDir).toAbsolutePath().normalize();
        String location = "file:" + dir + (dir.toString().endsWith("/") ? "" : "/");
        registry.addResourceHandler("/uploads/profiles/**")
                .addResourceLocations(location);
    }
}
