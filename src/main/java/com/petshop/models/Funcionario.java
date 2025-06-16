package com.petshop.models;

import jakarta.persistence.*;

@Entity
public class Funcionario extends Usuario {
    @Column(unique = true)
    private String matricula;

    // Construtor
    public Funcionario(String cpf, String nome, String matricula) {
        super(cpf, nome);
        this.matricula = matricula;
    }

    // Getter & Setter
    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
