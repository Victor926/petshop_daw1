package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clienteCpf = authentication.getName();
        model.addAttribute("clienteCpf", clienteCpf);
        return "dashboard-cliente";
    }
}