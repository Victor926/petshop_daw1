// com.petshop.models.Cliente.java

package com.petshop.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor; // Mantenha esta anotação

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor // Lombok vai gerar o construtor Cliente() {}
@Entity
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "dono", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();

    // Este construtor está correto e deve ser mantido
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