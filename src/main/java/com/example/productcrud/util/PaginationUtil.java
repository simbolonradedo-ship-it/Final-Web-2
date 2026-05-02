package com.example.productcrud.util;

import org.springframework.ui.Model;

public final class PaginationUtil {

    private static final int DEFAULT_WINDOW = 5;

    private PaginationUtil() {
    }

    public static void addPageWindow(Model model, int currentPage, int totalPages) {
        if (totalPages <= 0) {
            model.addAttribute("pageWindowStart", 0);
            model.addAttribute("pageWindowEnd", -1);
            return;
        }
        int windowSize = DEFAULT_WINDOW;
        int half = windowSize / 2;
        int start = Math.max(0, currentPage - half);
        int end = Math.min(totalPages - 1, start + windowSize - 1);
        start = Math.max(0, end - windowSize + 1);
        model.addAttribute("pageWindowStart", start);
        model.addAttribute("pageWindowEnd", end);
    }
}
