package com.petshop.controller;

import com.petshop.models.Cliente;
import com.petshop.models.Usuario; // Importar Usuario (se ainda não estiver importado)
import com.petshop.services.ClienteService; // Importar ClienteService
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- ESSA É A IMPORTAÇÃO QUE FALTAVA
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Não precisa de java.time.LocalDateTime aqui, a menos que você o use diretamente.

@Controller
public class CadastroClienteController {

    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder; // Para criptografar a senha

    public CadastroClienteController(ClienteService clienteService, PasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
    }

    // Exibir o formulário de cadastro
    @GetMapping("/cadastro/cliente")
    public String exibirFormularioCadastroCliente(Model model) {
        model.addAttribute("cliente", new Cliente()); // Objeto Cliente para o formulário
        return "cadastro-cliente"; // Nome do arquivo HTML do formulário de cadastro
    }

    // Processar o cadastro do cliente
    @PostMapping("/cadastro/cliente")
    public String cadastrarCliente(@ModelAttribute("cliente") Cliente cliente,
                                   @RequestParam("password") String password, // Receber a senha separadamente
                                   RedirectAttributes redirectAttributes) {
        try {
            // A lógica de criptografia e salvamento está no ClienteService
            clienteService.cadastrarNovoCliente(cliente, password);

            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login"; // Redireciona para a página de login após o cadastro
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro no cadastro: " + e.getMessage());
            return "redirect:/cadastro/cliente"; // Volta para o formulário em caso de erro
        }
    }
}