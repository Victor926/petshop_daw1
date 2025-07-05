package com.petshop.controller;

import com.petshop.models.Servico;
import com.petshop.repositories.ServicoRepository; // Usaremos o repositório diretamente para simplicidade neste CRUD admin
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/servicos") // URL base para a gestão de serviços pelo administrador
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
        return "servicos"; // Nome do arquivo HTML para exibir a lista de serviços
    }

    // EXIBIR FORMULÁRIO PARA NOVO SERVIÇO
    // Ex: GET /admin/servicos/novo
    @GetMapping("/novo")
    public String exibirFormularioNovoServico(Model model) {
        model.addAttribute("servico", new Servico()); // Objeto Servico vazio para o formulário
        return "servico-form"; // Nome do arquivo HTML do formulário de cadastro/edição
    }

    // EXIBIR FORMULÁRIO PARA EDITAR SERVIÇO
    // Ex: GET /admin/servicos/editar/{id}
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarServico(@PathVariable Long id, Model model) {
        Optional<Servico> servico = servicoRepository.findById(id);
        if (servico.isPresent()) {
            model.addAttribute("servico", servico.get());
            return "servico-form"; // Reusa o mesmo formulário
        }
        // Redireciona para a lista se o serviço não for encontrado
        return "redirect:/admin/servicos";
    }

    // SALVAR/ATUALIZAR SERVIÇO (via POST)
    // Ex: POST /admin/servicos/salvar
    @PostMapping("/salvar")
    public String salvarServico(@ModelAttribute Servico servico, RedirectAttributes redirectAttributes) {
        try {
            servicoRepository.save(servico);
            redirectAttributes.addFlashAttribute("mensagem", "Serviço salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar serviço: " + e.getMessage());
            // Se for uma edição e o ID existir, redireciona de volta ao formulário de edição
            if (servico.getId() != null) {
                return "redirect:/admin/servicos/editar/" + servico.getId();
            }
            // Caso contrário, redireciona para o formulário de novo serviço
            return "redirect:/admin/servicos/novo";
        }
        return "redirect:/admin/servicos"; // Redireciona para a lista de serviços após salvar
    }

    // DELETAR SERVIÇO
    // Ex: GET /admin/servicos/deletar/{id} (Geralmente DELETE, mas GET para simplicidade inicial)
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