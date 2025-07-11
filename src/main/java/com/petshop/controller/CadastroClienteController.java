package com.petshop.controller;

import com.petshop.models.Cliente;
import com.petshop.models.Usuario;
import com.petshop.services.ClienteService; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
public class CadastroClienteController {

    private final ClienteService clienteService;
    private final PasswordEncoder passwordEncoder;

    public CadastroClienteController(ClienteService clienteService, PasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.passwordEncoder = passwordEncoder;
    }

    // Exibir o formulário de cadastro
    @GetMapping("/cadastro/cliente")
    public String exibirFormularioCadastroCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cadastro-cliente";
    }

    // Processar o cadastro do cliente
    @PostMapping("/cadastro/cliente")
    public String cadastrarCliente(@ModelAttribute("cliente") Cliente cliente,
                                   @RequestParam("password") String password,
                                   RedirectAttributes redirectAttributes) {
        try {
            clienteService.cadastrarNovoCliente(cliente, password);

            redirectAttributes.addFlashAttribute("mensagem", "Cadastro realizado com sucesso! Faça login para continuar.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro no cadastro: " + e.getMessage());
            return "redirect:/cadastro/cliente"; // Volta para o formulário em caso de erro
        }
    }
}