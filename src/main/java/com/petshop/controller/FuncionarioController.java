package com.petshop.controller;

import com.petshop.models.Funcionario;
import com.petshop.repositories.FuncionarioRepository; // Usaremos o repositório diretamente para simplicidade neste CRUD admin
import com.petshop.repositories.HorarioDisponivelRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final PasswordEncoder passwordEncoder; // NOVO CAMPO

    public FuncionarioController(FuncionarioRepository funcionarioRepository,
                                 HorarioDisponivelRepository horarioDisponivelRepository,
                                 PasswordEncoder passwordEncoder) { // NOVO PARÂMETRO NO CONSTRUTOR
        this.funcionarioRepository = funcionarioRepository;
        this.horarioDisponivelRepository = horarioDisponivelRepository;
        this.passwordEncoder = passwordEncoder; // INJEÇÃO
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
    @PostMapping("/salvar")
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario,
                                    @RequestParam(value = "senha", required = false) String senha, // Captura a senha do formulário
                                    RedirectAttributes redirectAttributes) {
        try {
            // Lógica para criptografar a senha e manter a existente se for edição
            if (senha != null && !senha.isEmpty()) {
                funcionario.setSenha(passwordEncoder.encode(senha)); // Criptografa a nova senha
            } else if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                // Se for edição e a senha não foi fornecida, busca a senha existente do DB
                String senhaExistente = funcionarioRepository.findById(funcionario.getCpf())
                                                             .map(Funcionario::getSenha)
                                                             .orElseThrow(() -> new IllegalArgumentException("Funcionário existente não encontrado para manter a senha."));
                funcionario.setSenha(senhaExistente);
            } else if (funcionario.getCpf() == null || senha == null || senha.isEmpty()) {
                // Se for um novo funcionário, a senha é obrigatória
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
            // Para depuração, você pode querer logar a stack trace completa aqui.
            // e.printStackTrace();
            if (funcionario.getCpf() != null && funcionarioRepository.existsById(funcionario.getCpf())) {
                return "redirect:/admin/funcionarios/editar/" + funcionario.getCpf();
            }
            return "redirect:/admin/funcionarios/novo";
        }
        return "redirect:/admin/funcionarios";
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
