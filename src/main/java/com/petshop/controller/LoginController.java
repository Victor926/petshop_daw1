package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Rota para exibir a página de login customizada
    @GetMapping("/login")
    public String login() {
        return "login"; // Retorna o nome do arquivo HTML: login.html
    }

    // Rota para a página de login do cliente
    @GetMapping("/login/cliente")
    public String loginCliente() {
        // Você pode adicionar lógica específica ou apenas redirecionar para a página de login geral
        return "login"; // Por enquanto, ambos usam a mesma página de login
    }

    // Rota para a página de login do administrador
    @GetMapping("/login/admin")
    public String loginAdmin() {
        // Você pode adicionar lógica específica ou apenas redirecionar para a página de login geral
        return "login"; // Por enquanto, ambos usam a mesma página de login
    }
}
