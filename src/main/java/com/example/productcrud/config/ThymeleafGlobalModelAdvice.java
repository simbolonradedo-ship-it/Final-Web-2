package com.example.productcrud.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Thymeleaf 3.1+ tidak lagi menyediakan {@code #request} di ekspresi template.
 * Path servlet dipasok ke model agar fragment layout bisa menandai menu aktif.
 */
@ControllerAdvice
public class ThymeleafGlobalModelAdvice {

    @ModelAttribute("servletPath")
    public String servletPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null ? path : "";
    }
}
