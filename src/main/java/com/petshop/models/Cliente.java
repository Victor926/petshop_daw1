package com.petshop.models;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private List<Pet> pets = new ArrayList<>();

    // Construtor
    public Cliente(String cpf, String nome) {
        super(cpf, nome);
    }

    // Adiciona um pet ao cliente
    public void adicionarPet(Pet pet) {
        pets.add(pet);
    }

    // Getter
    public List<Pet> getPets() {
        return pets;
    }
}
