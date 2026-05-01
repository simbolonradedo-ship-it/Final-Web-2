package com.example.productcrud.controller;

import com.example.productcrud.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", dashboardService.getTotalProducts());
        model.addAttribute("activeProducts", dashboardService.getActiveProducts());
        model.addAttribute("inactiveProducts", dashboardService.getInactiveProducts());
        model.addAttribute("totalStock", dashboardService.getTotalStock());
        model.addAttribute("totalValue", dashboardService.getTotalValue());
        return "dashboard";
    }
}