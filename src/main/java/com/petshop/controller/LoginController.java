package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {


    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    // Rota para a página de login do cliente
    @GetMapping("/login/cliente")
    public String loginCliente() {
        return "login"; 
    }

    // Rota para a página de login do administrador
    @GetMapping("/login/admin")
    public String loginAdmin() {
        return "login";
    }
}
