package com.petshop.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Data; // Adicione esta importação para o Lombok @Data

import java.util.ArrayList;
import java.util.List;

@Data // Adicione esta anotação para gerar getters e setters
@NoArgsConstructor
@Entity
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "dono", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();

    public Cliente(String cpf, String nome, String senha) {
        super(cpf, nome, senha); // Chama o construtor de Usuario com senha
    }

    // Adiciona um pet ao cliente (pode deixar, é um método de negócio)
    // public void adicionarPet(Pet pet) {
    // pets.add(pet);
    // }

    // Getter (Lombok @Data já gera, mas se quiser explícito, mantenha)
    // public List<Pet> getPets() {
    // return pets;
    // }
}