package com.petshop.controller;

import com.petshop.models.Funcionario;
import com.petshop.repositories.FuncionarioRepository; // Usaremos o repositório diretamente para simplicidade neste CRUD admin
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/funcionarios") // URL base para a gestão de funcionários pelo administrador
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    // LISTAR TODOS OS FUNCIONÁRIOS
    // Ex: GET /admin/funcionarios
    @GetMapping
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios"; // Nome do arquivo HTML para exibir a lista de funcionários
    }

    // EXIBIR FORMULÁRIO PARA NOVO FUNCIONÁRIO
    // Ex: GET /admin/funcionarios/novo
    @GetMapping("/novo")
    public String exibirFormularioNovoFuncionario(Model model) {
        model.addAttribute("funcionario", new Funcionario()); // Objeto Funcionario vazio para o formulário
        return "funcionario-form"; // Nome do arquivo HTML do formulário de cadastro/edição
    }

    // EXIBIR FORMULÁRIO PARA EDITAR FUNCIONÁRIO
    // Ex: GET /admin/funcionarios/editar/{cpf}
    @GetMapping("/editar/{cpf}")
    public String exibirFormularioEditarFuncionario(@PathVariable String cpf, Model model) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(cpf);
        if (funcionario.isPresent()) {
            model.addAttribute("funcionario", funcionario.get());
            return "funcionario-form"; // Reusa o mesmo formulário
        }
        // Redireciona para a lista se o funcionário não for encontrado
        return "redirect:/admin/funcionarios";
    }

    // SALVAR/ATUALIZAR FUNCIONÁRIO (via POST)
    // Ex: POST /admin/funcionarios/salvar
    @PostMapping("/salvar")
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario, RedirectAttributes redirectAttributes) {
        try {
            funcionarioRepository.save(funcionario);
            redirectAttributes.addFlashAttribute("mensagem", "Funcionário salvo com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar funcionário: " + e.getMessage());
            // Se for uma edição e o CPF existir, redireciona de volta ao formulário de edição
            if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                return "redirect:/admin/funcionarios/editar/" + funcionario.getCpf();
            }
            // Caso contrário, redireciona para o formulário de novo funcionário
            return "redirect:/admin/funcionarios/novo";
        }
        return "redirect:/admin/funcionarios"; // Redireciona para a lista de funcionários após salvar
    }

    // DELETAR FUNCIONÁRIO
    // Ex: GET /admin/funcionarios/deletar/{cpf} (Geralmente DELETE, mas GET para simplicidade inicial)
    @GetMapping("/deletar/{cpf}")
    public String deletarFuncionario(@PathVariable String cpf, RedirectAttributes redirectAttributes) {
        try {
            funcionarioRepository.deleteById(cpf);
            redirectAttributes.addFlashAttribute("mensagem", "Funcionário excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir funcionário: " + e.getMessage());
        }
        return "redirect:/admin/funcionarios";
    }
}
