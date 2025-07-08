// src/main/java/com/petshop/controller/ServicoClienteController.java
package com.petshop.controller;

import com.petshop.models.Servico;
import com.petshop.services.ServicoService; // Precisamos do Service de Serviço
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication; // Para pegar o usuário logado
import org.springframework.security.core.context.SecurityContextHolder; // Para pegar o usuário logado

import java.util.List;

@Controller
@RequestMapping("/cliente") // Mapeamento base para as funcionalidades do cliente
public class ServicoClienteController {

    private final ServicoService servicoService; // Injetando o ServicoService

    public ServicoClienteController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    // ATENÇÃO AQUI: Método para listar serviços disponíveis para o cliente
    // Acessível via /cliente/{clienteCpf}/servicos
    @GetMapping("/{clienteCpf}/servicos")
    public String listarServicosParaCliente(@PathVariable String clienteCpf, Model model) {
        // Validação básica: garante que o CPF na URL pertence ao usuário logado
        // Isso é uma medida de segurança importante para evitar que um cliente veja dados de outro
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInCpf = authentication.getName(); // CPF do usuário logado

        if (!loggedInCpf.equals(clienteCpf)) {
            // Se o CPF na URL não for o do usuário logado, redireciona ou mostra erro
            model.addAttribute("erro", "Você não tem permissão para acessar os serviços de outro cliente.");
            return "redirect:/cliente/" + loggedInCpf + "/dashboard"; // Redireciona para o próprio dashboard
        }

        List<Servico> servicos = servicoService.findAllServicos(); // Busca todos os serviços disponíveis
        model.addAttribute("servicos", servicos);
        model.addAttribute("clienteCpf", clienteCpf); // Passa o CPF para o modelo, essencial para os links de voltar
        return "servicos-cliente"; // Retorna o nome do seu novo HTML
    }
}