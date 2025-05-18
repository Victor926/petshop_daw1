package com.petshop.models;

public class Funcionario extends Usuario {
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
