package com.petshop.controller;

import com.petshop.models.Servico;
import com.petshop.services.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Controller
@RequestMapping("/cliente")
public class ServicoClienteController {

    private final ServicoService servicoService;

    public ServicoClienteController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    //Método para listar serviços disponíveis para o cliente
    @GetMapping("/{clienteCpf}/servicos")
    public String listarServicosParaCliente(@PathVariable String clienteCpf, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInCpf = authentication.getName();
        if (!loggedInCpf.equals(clienteCpf)) {
            model.addAttribute("erro", "Você não tem permissão para acessar os serviços de outro cliente.");
            return "redirect:/cliente/" + loggedInCpf + "/dashboard";
        }

        List<Servico> servicos = servicoService.findAllServicos();
        model.addAttribute("servicos", servicos);
        model.addAttribute("clienteCpf", clienteCpf);
        return "servicos-cliente";
    }
}