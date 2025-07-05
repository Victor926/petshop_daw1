package com.petshop.services;

import com.petshop.models.Pet;
import com.petshop.models.Cliente;
import com.petshop.repositories.PetRepository;
import com.petshop.repositories.ClienteRepository; // Precisamos do ClienteRepository para buscar o cliente
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final ClienteRepository clienteRepository; // Adicionado para poder associar o pet ao cliente

    public PetService(PetRepository petRepository, ClienteRepository clienteRepository) {
        this.petRepository = petRepository;
        this.clienteRepository = clienteRepository;
    }

    // Método para salvar/atualizar um Pet
    public Pet savePet(Pet pet, String clienteCpf) {
        // Encontra o cliente pelo CPF
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteCpf);
        if (clienteOptional.isPresent()) {
            Cliente dono = clienteOptional.get();
            pet.setDono(dono); // Associa o pet ao cliente
            return petRepository.save(pet);
        } else {
            // Lançar uma exceção ou lidar com o caso de cliente não encontrado
            throw new IllegalArgumentException("Cliente com CPF " + clienteCpf + " não encontrado.");
        }
    }

    // Método para buscar todos os Pets de um Cliente
    public List<Pet> findPetsByClienteCpf(String clienteCpf) {
        // Você pode adicionar um método findByDonoCpf(String cpf) no PetRepository se quiser uma query mais específica,
        // mas por enquanto, podemos pegar o cliente e acessar a lista de pets dele (com cuidado para inicialização LAZY)
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteCpf);
        if (clienteOptional.isPresent()) {
            // Garante que a coleção de pets seja carregada
            return clienteOptional.get().getPets();
        }
        return List.of(); // Retorna uma lista vazia se o cliente não for encontrado
    }

    // Método para buscar um Pet pelo ID
    public Optional<Pet> findPetById(Long id) {
        return petRepository.findById(id);
    }

    // Método para deletar um Pet
    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }
}