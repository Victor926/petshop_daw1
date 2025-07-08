package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication; // Importar Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Importar SecurityContextHolder

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/cliente/dashboard")
    public String clienteDashboard(Model model) { // Adicionado Model como parâmetro
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String clienteCpf = authentication.getName(); // O CPF é o 'name' do Principal no UserDetailsServiceImpl
        model.addAttribute("clienteCpf", clienteCpf); // ATENÇÃO AQUI: Passando o CPF para o modelo
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