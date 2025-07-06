package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard() {
        return "dashboard-cliente";
    }

    // REMOVA OU COMENTE ESTE MÉTODO, pois ele agora está no AdminController
    /*
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }
    */
}