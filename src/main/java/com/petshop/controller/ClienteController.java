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

    // SALVAR/ATUALIZAR CLIENTE (via POST) - LÓGICA ATUALIZADA PARA NÃO DUPLICAR (COM DEBUG)
    @PostMapping("/salvar")
    @Transactional
    public String salvarCliente(@ModelAttribute Cliente clienteDoFormulario,
                                @RequestParam(value = "senha", required = false) String senha,
                                RedirectAttributes redirectAttributes) {
        try {
            // --- INÍCIO DEBUG CLIENTE SALVAR NO CONTROLLER ---
            System.out.println("--- DEBUG CLIENTE SALVAR INÍCIO ---");
            System.out.println("CPF do Formulário (clienteDoFormulario.getCpf()): " + clienteDoFormulario.getCpf());
            System.out.println("Nome do Formulário (clienteDoFormulario.getNome()): " + clienteDoFormulario.getNome());
            System.out.println("Senha do Formulário (variável 'senha'): " + (senha != null && !senha.isEmpty() ? "******" : "[VAZIA]"));

            Optional<Cliente> clienteExistenteOptional = clienteRepository.findById(clienteDoFormulario.getCpf());
            System.out.println("Cliente Existente Optional Presente? " + clienteExistenteOptional.isPresent());

            Cliente clienteAAtualizar; // Esta será a entidade que será salva (existente ou nova)

            if (clienteExistenteOptional.isPresent()) {
                System.out.println("Caminho: EDIÇÃO DE CLIENTE EXISTENTE NO DB.");
                clienteAAtualizar = clienteExistenteOptional.get();
                System.out.println("Hash de Identidade clienteAAtualizar (do DB): " + System.identityHashCode(clienteAAtualizar));
                System.out.println("Hash de Identidade clienteExistenteOptional.get(): " + System.identityHashCode(clienteExistenteOptional.get()));
                
                // Atualizar APENAS os campos do cliente gerenciado com os dados do formulário
                clienteAAtualizar.setNome(clienteDoFormulario.getNome()); 

                // Lógica da senha para edição
                if (senha != null && !senha.isEmpty()) {
                    clienteAAtualizar.setSenha(passwordEncoder.encode(senha)); // Criptografa nova senha
                    System.out.println("Senha atualizada para clienteAAtualizar.");
                } else {
                    System.out.println("Senha mantida para clienteAAtualizar (vinda do DB).");
                }
                
                // Validação para impedir que um Cliente seja transformado em Funcionario por aqui
                if (usuarioRepository.existsById(clienteDoFormulario.getCpf())) {
                    if (usuarioRepository.findById(clienteDoFormulario.getCpf()).get() instanceof com.petshop.models.Funcionario) {
                        System.out.println("Erro: CPF existe como Funcionário.");
                        throw new IllegalArgumentException("CPF já cadastrado como Funcionário. Não é possível editar como Cliente.");
                    }
                }

            } else {
                System.out.println("Caminho: CRIAÇÃO DE NOVO CLIENTE (CPF não encontrado no DB).");
                // É uma CRIAÇÃO de um NOVO cliente:
                if (senha == null || senha.isEmpty()) {
                    System.out.println("Erro: Senha obrigatória para novo cliente.");
                    throw new IllegalArgumentException("A senha é obrigatória para um novo cliente.");
                }
                // Validação de CPF duplicado ao criar
                if (usuarioRepository.existsById(clienteDoFormulario.getCpf())) {
                    System.out.println("Erro: CPF já cadastrado como Usuário.");
                    throw new IllegalArgumentException("CPF já cadastrado no sistema. Por favor, use outro CPF.");
                }
                
                clienteAAtualizar = new Cliente(); // Cria uma nova instância de Cliente
                clienteAAtualizar.setCpf(clienteDoFormulario.getCpf());
                clienteAAtualizar.setNome(clienteDoFormulario.getNome());
                clienteAAtualizar.setSenha(passwordEncoder.encode(senha)); // Criptografa a senha para o novo cliente
                System.out.println("Nova instância de Cliente criada com CPF: " + clienteAAtualizar.getCpf());
                System.out.println("Hash de Identidade clienteAAtualizar (nova instância): " + System.identityHashCode(clienteAAtualizar));
            }

            System.out.println("STATUS FINAL ANTES DO SAVE - CPF: " + clienteAAtualizar.getCpf());
            System.out.println("STATUS FINAL ANTES DO SAVE - Nome: " + clienteAAtualizar.getNome());
            System.out.println("STATUS FINAL ANTES DO SAVE - Tipo: " + clienteAAtualizar.getClass().getSimpleName());
            System.out.println("STATUS FINAL ANTES DO SAVE - Hash: " + System.identityHashCode(clienteAAtualizar));
            System.out.println("--- FIM DEBUG CLIENTE SALVAR ---");
            // --- FIM DEBUG ---

            clienteRepository.save(clienteAAtualizar); // Salva a entidade

            redirectAttributes.addFlashAttribute("mensagem", "Cliente salvo com sucesso!");

        } catch (IllegalArgumentException e) {
            System.err.println("Capturado IllegalArgumentException: " + e.getMessage()); // Debug de erro
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar cliente: " + e.getMessage());
            if (clienteDoFormulario.getCpf() != null && clienteRepository.existsById(clienteDoFormulario.getCpf())) {
                return "redirect:/admin/clientes/editar/" + clienteDoFormulario.getCpf();
            }
            return "redirect:/admin/clientes/novo";
        } catch (Exception e) {
            System.err.println("Capturado Exception: " + e.getMessage()); // Debug de erro
            e.printStackTrace(); // MUITO IMPORTANTE MANTER PARA VER O ERRO REAL
            redirectAttributes.addFlashAttribute("erro", "Erro interno ao salvar cliente: " + e.getMessage());
            if (clienteDoFormulario.getCpf() != null && clienteRepository.existsById(clienteDoFormulario.getCpf())) {
                return "redirect:/admin/clientes/editar/" + clienteDoFormulario.getCpf();
            }
            return "redirect:/admin/clientes/novo";
        }
        return "redirect:/admin/clientes";
    }


    // DELETAR CLIENTE - A LÓGICA DE EXCLUSÃO DE PETS AINDA DEVE ESTAR NO SERVICE
    @GetMapping("/deletar/{cpf}")
    public String deletarCliente(@PathVariable String cpf, RedirectAttributes redirectAttributes) {
        try {
            clienteService.deleteCliente(cpf); // Chama o serviço para a exclusão completa
            redirectAttributes.addFlashAttribute("mensagem", "Cliente excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir cliente: " + e.getMessage());
        }
        return "redirect:/admin/clientes";
    }
}