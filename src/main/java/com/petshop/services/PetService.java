package com.petshop.services;

import com.petshop.models.Pet;
import com.petshop.models.Cliente;
import com.petshop.repositories.PetRepository;
import com.petshop.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final ClienteRepository clienteRepository;

    public PetService(PetRepository petRepository, ClienteRepository clienteRepository) {
        this.petRepository = petRepository;
        this.clienteRepository = clienteRepository;
    }

    // Método para salvar/atualizar um Pet
    public Pet savePet(Pet pet, String clienteCpf) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteCpf);
        if (clienteOptional.isPresent()) {
            Cliente dono = clienteOptional.get();
            pet.setDono(dono);
            return petRepository.save(pet);
        } else {
            throw new IllegalArgumentException("Cliente com CPF " + clienteCpf + " não encontrado.");
        }
    }

    // Método para buscar todos os Pets de um Cliente
    public List<Pet> findPetsByClienteCpf(String clienteCpf) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteCpf);
        if (clienteOptional.isPresent()) {
            return clienteOptional.get().getPets();
        }
        return List.of();
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