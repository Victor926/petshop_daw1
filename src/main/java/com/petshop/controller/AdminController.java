package com.petshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin-dashboard"; // Retorna o nome do arquivo HTML: admin-dashboard.html
    }

    // REMOVA OU COMENTE OS MÉTODOS ABAIXO, pois eles já são tratados por outros controladores (FuncionarioController, ServicoController, etc.)
    // Exemplo do que deve ser REMOVIDO:
    /*
    @GetMapping("/servicos")
    public String gerenciarServicos() {
        return "admin/servicos"; // Isso entra em conflito com ServicoController
    }

    @GetMapping("/funcionarios")
    public String gerenciarFuncionarios() {
        return "admin/funcionarios"; // Isso entra em conflito com FuncionarioController
    }

    @GetMapping("/horarios")
    public String gerenciarHorarios() {
        return "admin/horarios"; // Isso entra em conflito com HorarioDisponivelController
    }
    */
}