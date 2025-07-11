package com.petshop.controller;

import com.petshop.models.Funcionario;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.HorarioDisponivelRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioController(FuncionarioRepository funcionarioRepository,
                                 HorarioDisponivelRepository horarioDisponivelRepository,
                                 PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.horarioDisponivelRepository = horarioDisponivelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LISTAR TODOS OS FUNCIONÁRIOS
    @GetMapping
    public String listarFuncionarios(Model model) {
        List<Funcionario> funcionarios = funcionarioRepository.findAll();
        model.addAttribute("funcionarios", funcionarios);
        return "funcionarios";
    }

    // EXIBIR FORMULÁRIO PARA NOVO FUNCIONÁRIO
    @GetMapping("/novo")
    public String exibirFormularioNovoFuncionario(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        return "funcionario-form";
    }

    // EXIBIR FORMULÁRIO PARA EDITAR FUNCIONÁRIO
    @GetMapping("/editar/{cpf}")
    public String exibirFormularioEditarFuncionario(@PathVariable String cpf, Model model) {
        Optional<Funcionario> funcionario = funcionarioRepository.findById(cpf);
        if (funcionario.isPresent()) {
            model.addAttribute("funcionario", funcionario.get());
            return "funcionario-form";
        }
        return "redirect:/admin/funcionarios";
    }

    // SALVAR/ATUALIZAR FUNCIONÁRIO
    @PostMapping("/salvar")
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario,
                                    @RequestParam(value = "senha", required = false) String senha,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Lógica para criptografar a senha e manter a existente se for edição
            if (senha != null && !senha.isEmpty()) {
                funcionario.setSenha(passwordEncoder.encode(senha));
            } else if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                String senhaExistente = funcionarioRepository.findById(funcionario.getCpf())
                                                             .map(Funcionario::getSenha)
                                                             .orElseThrow(() -> new IllegalArgumentException("Funcionário existente não encontrado para manter a senha."));
                funcionario.setSenha(senhaExistente);
            } else if (funcionario.getCpf() == null || senha == null || senha.isEmpty()) {
                throw new IllegalArgumentException("A senha é obrigatória para um novo funcionário.");
            }

            funcionarioRepository.save(funcionario);
            redirectAttributes.addFlashAttribute("mensagem", "Funcionário salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                return "redirect:/admin/funcionarios/editar/" + funcionario.getCpf();
            }
            return "redirect:/admin/funcionarios/novo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar funcionário: " + e.getMessage());
            if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                return "redirect:/admin/funcionarios/editar/" + funcionario.getCpf();
            }
            return "redirect:/admin/funcionarios/novo";
        }
        return "redirect:/admin/funcionarios";
    }


    // DELETAR FUNCIONÁRIO
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
