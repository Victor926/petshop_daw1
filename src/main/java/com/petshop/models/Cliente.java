package com.petshop.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "dono", cascade = CascadeType.ALL)
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
