package com.petshop.controller;

import com.petshop.models.Servico;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/servicos")
public class ServicoController {

    private final ServicoRepository servicoRepository;

    public ServicoController(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    // LISTAR TODOS OS SERVIÇOS
    // Ex: GET /admin/servicos
    @GetMapping
    public String listarServicos(Model model) {
        List<Servico> servicos = servicoRepository.findAll();
        model.addAttribute("servicos", servicos);
        return "servicos";
    }

    // EXIBIR FORMULÁRIO PARA NOVO SERVIÇO
    @GetMapping("/novo")
    public String exibirFormularioNovoServico(Model model) {
        model.addAttribute("servico", new Servico());
        return "servico-form";
    }

    // EXIBIR FORMULÁRIO PARA EDITAR SERVIÇO
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarServico(@PathVariable Long id, Model model) {
        Optional<Servico> servico = servicoRepository.findById(id);
        if (servico.isPresent()) {
            model.addAttribute("servico", servico.get());
            return "servico-form";
        }
        return "redirect:/admin/servicos";
    }

    // SALVAR/ATUALIZAR SERVIÇO
    @PostMapping("/salvar")
    public String salvarServico(@ModelAttribute Servico servico, RedirectAttributes redirectAttributes) {
        try {
            servicoRepository.save(servico);
            redirectAttributes.addFlashAttribute("mensagem", "Serviço salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar serviço: " + e.getMessage());
            if (servico.getId() != null) {
                return "redirect:/admin/servicos/editar/" + servico.getId();
            }
            return "redirect:/admin/servicos/novo";
        }
        return "redirect:/admin/servicos";
    }

    // DELETAR SERVIÇO
    @GetMapping("/deletar/{id}")
    public String deletarServico(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            servicoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensagem", "Serviço excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir serviço: " + e.getMessage());
        }
        return "redirect:/admin/servicos";
    }
}