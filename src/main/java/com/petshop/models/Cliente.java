package com.petshop.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class Cliente extends Usuario {
    @OneToMany(mappedBy = "dono", cascade = CascadeType.ALL)
    private List<Pet> pets = new ArrayList<>();

    public Cliente(String cpf, String nome, String senha) {
        super(cpf, nome, senha);
    }
}