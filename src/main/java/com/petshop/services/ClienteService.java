package com.petshop.services;

import com.petshop.models.Cliente;
import com.petshop.models.Usuario; // Importar Usuario
import com.petshop.repositories.ClienteRepository;
import com.petshop.repositories.UsuarioRepository; // Importar UsuarioRepository
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Para garantir que ambas as operações sejam atômicas

import java.util.Optional;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // MÉTODO CADASTRARNOVOCLIENTE CORRIGIDO
    @Transactional
    public Cliente cadastrarNovoCliente(Cliente cliente, String password) {
        if (cliente == null || cliente.getCpf() == null || cliente.getNome() == null || password == null) {
            throw new IllegalArgumentException("Dados do cliente ou senha inválidos.");
        }

        // 1. Verificar se o CPF já existe como USUARIO
        // Agora que Cliente é um Usuario, podemos usar o findById do UsuarioRepository no CPF
        if (usuarioRepository.findById(cliente.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }

        // 2. Definir a senha CRIPTOGRAFADA DIRETAMENTE NO OBJETO CLIENTE
        // Como Cliente herda de Usuario, ele tem o método setSenha()
        cliente.setSenha(passwordEncoder.encode(password)); // Criptografar e definir a senha

        // 3. Salvar o Cliente. O JPA/Hibernate cuidará de inserir os dados na tabela 'usuario'
        // e na tabela 'cliente' automaticamente devido à herança e mapeamento.
        return clienteRepository.save(cliente);
    }

    // O restante da classe permanece o mesmo: findClienteByCpf, findAll, save, deleteByCpf
    public Optional<Cliente> findClienteByCpf(String cpf) {
        return clienteRepository.findById(cpf);
    }

    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    public Cliente save(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public void deleteByCpf(String cpf) {
        clienteRepository.deleteById(cpf);
        usuarioRepository.deleteById(cpf); // Deleta da tabela 'usuario' também
    }
}