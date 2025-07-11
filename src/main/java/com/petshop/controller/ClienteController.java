package com.petshop.controller;

import com.petshop.models.Cliente;
import com.petshop.repositories.ClienteRepository;
import com.petshop.repositories.UsuarioRepository;
import com.petshop.repositories.PetRepository;
import com.petshop.services.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/admin/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteController(ClienteService clienteService,
                             ClienteRepository clienteRepository,
                             UsuarioRepository usuarioRepository,
                             PetRepository petRepository,
                             PasswordEncoder passwordEncoder) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // LISTAR TODOS OS CLIENTES
    @GetMapping
    public String listarClientes(Model model) {
        List<Cliente> clientes = clienteService.findAllClientes();
        model.addAttribute("clientes", clientes);
        return "clientes";
    }

    // EXIBIR FORMULÁRIO PARA NOVO CLIENTE (Admin cria)
    @GetMapping("/novo")
    public String exibirFormularioNovoCliente(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente-form";
    }

    // NOVO MÉTODO: SALVAR NOVO CLIENTE
    @PostMapping("/novo")
    @Transactional
    public String criarNovoCliente(@ModelAttribute Cliente clienteDoFormulario,
                                   @RequestParam("senha") String senha,
                                   RedirectAttributes redirectAttributes) {
        try {
            System.out.println("--- DEBUG CRIAÇÃO NOVO CLIENTE INÍCIO ---");
            System.out.println("CPF do Formulário: " + clienteDoFormulario.getCpf());

            // Validação de CPF duplicado
            if (usuarioRepository.existsById(clienteDoFormulario.getCpf())) {
                System.out.println("Erro: CPF já cadastrado como Usuário.");
                throw new IllegalArgumentException("CPF já cadastrado no sistema. Por favor, use outro CPF.");
            }
            if (senha == null || senha.isEmpty()) {
                System.out.println("Erro: Senha obrigatória para novo cliente.");
                throw new IllegalArgumentException("A senha é obrigatória para um novo cliente.");
            }

            Cliente novoCliente = new Cliente();
            novoCliente.setCpf(clienteDoFormulario.getCpf());
            novoCliente.setNome(clienteDoFormulario.getNome());
            novoCliente.setSenha(passwordEncoder.encode(senha));

            clienteRepository.save(novoCliente);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
            System.out.println("--- FIM DEBUG CRIAÇÃO NOVO CLIENTE ---");
        } catch (IllegalArgumentException e) {
            System.err.println("Capturado IllegalArgumentException (Criação): " + e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar cliente: " + e.getMessage());
            return "redirect:/admin/clientes/novo";
        } catch (Exception e) {
            System.err.println("Capturado Exception (Criação): " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro interno ao cadastrar cliente: " + e.getMessage());
            return "redirect:/admin/clientes/novo";
        }
        return "redirect:/admin/clientes";
    }

    // EXIBIR FORMULÁRIO PARA EDITAR CLIENTE
    @GetMapping("/editar/{cpf}")
    public String exibirFormularioEditarCliente(@PathVariable String cpf, Model model) {
        Optional<Cliente> cliente = clienteService.findClienteByCpf(cpf);
        if (cliente.isPresent()) {
            model.addAttribute("cliente", cliente.get());
            return "cliente-form";
        }
        return "redirect:/admin/clientes";
    }

    // NOVO MÉTODO: ATUALIZAR CLIENTE EXISTENTE
    @PostMapping("/editar/{cpf}")
    @Transactional
    public String atualizarCliente(@PathVariable String cpf,
                                   @ModelAttribute Cliente clienteDoFormulario,
                                   @RequestParam(value = "senha", required = false) String senha,
                                   RedirectAttributes redirectAttributes) {
        try {
            System.out.println("--- DEBUG EDIÇÃO CLIENTE INÍCIO ---");
            System.out.println("CPF do Path: " + cpf);
            System.out.println("Nome do Formulário: " + clienteDoFormulario.getNome());
            System.out.println("Senha do Formulário (variável 'senha'): " + (senha != null && !senha.isEmpty() ? "******" : "[VAZIA]"));

            Optional<Cliente> clienteExistenteOptional = clienteRepository.findById(cpf);

            if (clienteExistenteOptional.isEmpty()) {
                System.out.println("Erro: Cliente não encontrado para edição.");
                throw new IllegalArgumentException("Cliente não encontrado para edição.");
            }

            Cliente clienteAAtualizar = clienteExistenteOptional.get();
            System.out.println("Caminho: EDIÇÃO DE CLIENTE EXISTENTE NO DB.");

            // Validação para impedir que um Cliente seja transformado em Funcionario por aqui
            if (usuarioRepository.existsById(cpf)) {
                if (usuarioRepository.findById(cpf).get() instanceof com.petshop.models.Funcionario) {
                    System.out.println("Erro: CPF existe como Funcionário.");
                    throw new IllegalArgumentException("CPF já cadastrado como Funcionário. Não é possível editar como Cliente.");
                }
            }

            clienteAAtualizar.setNome(clienteDoFormulario.getNome());

            // Lógica da senha para edição
            if (senha != null && !senha.isEmpty()) {
                clienteAAtualizar.setSenha(passwordEncoder.encode(senha));
                System.out.println("Senha atualizada.");
            } else {
                System.out.println("Senha mantida (vinda do DB).");
            }

            clienteRepository.save(clienteAAtualizar);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente atualizado com sucesso!");
            System.out.println("--- FIM DEBUG EDIÇÃO CLIENTE ---");

        } catch (IllegalArgumentException e) {
            System.err.println("Capturado IllegalArgumentException (Edição): " + e.getMessage());
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar cliente: " + e.getMessage());
            return "redirect:/admin/clientes/editar/" + cpf;
        } catch (Exception e) {
            System.err.println("Capturado Exception (Edição): " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("erro", "Erro interno ao atualizar cliente: " + e.getMessage());
            return "redirect:/admin/clientes/editar/" + cpf;
        }
        return "redirect:/admin/clientes";
    }

    // DELETAR CLIENTE 
    @GetMapping("/deletar/{cpf}")
    public String deletarCliente(@PathVariable String cpf, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deleteCliente(cpf);
            redirectAttributes.addFlashAttribute("mensagem", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}