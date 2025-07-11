package com.petshop.services;

import com.petshop.models.Cliente;
import com.petshop.repositories.ClienteRepository;
import com.petshop.repositories.UsuarioRepository;
import com.petshop.repositories.PetRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PetRepository petRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository,
                          UsuarioRepository usuarioRepository,
                          PetRepository petRepository,
                          PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.petRepository = petRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Método para auto-cadastro de cliente
    @Transactional
    public Cliente cadastrarNovoCliente(Cliente cliente, String senha) {
        if (usuarioRepository.existsById(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }
        cliente.setSenha(passwordEncoder.encode(senha));
        return clienteRepository.save(cliente);
    }

    // Listar todos os clientes
    public List<Cliente> findAllClientes() {
        return clienteRepository.findAll();
    }

    // Buscar cliente por CPF
    public Optional<Cliente> findClienteByCpf(String cpf) {
        return clienteRepository.findById(cpf);
    }

    // Deletar cliente (mantido, pois lida com pets e a parte Usuario)
    @Transactional
    public void deleteCliente(String cpf) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(cpf);
        if (clienteOptional.isPresent()) {
            Cliente clienteToDelete = clienteOptional.get();

            petRepository.deleteByDonoCpf(cpf);

            clienteRepository.delete(clienteToDelete);
        } else {
            throw new IllegalArgumentException("Cliente com CPF " + cpf + " não encontrado para exclusão.");
        }
    }
}