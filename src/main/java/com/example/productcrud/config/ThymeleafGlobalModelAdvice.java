package com.example.productcrud.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ThymeleafGlobalModelAdvice {

    @ModelAttribute("servletPath")
    public String servletPath(HttpServletRequest request) {
        String path = request.getServletPath();
        return path != null ? path : "";
    }
}
