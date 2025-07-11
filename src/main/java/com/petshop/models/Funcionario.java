package com.petshop.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
@Entity
public class Funcionario extends Usuario {
    @Column(unique = true)
    private String matricula;

    public Funcionario(String cpf, String nome, String senha, String matricula) {
        super(cpf, nome, senha);
        this.matricula = matricula;
    }
}