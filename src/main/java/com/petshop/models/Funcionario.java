package com.petshop.models;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Data; // Adicione esta importação para o Lombok @Data

@Data // Adicione esta anotação para gerar getters e setters
@NoArgsConstructor
@Entity
public class Funcionario extends Usuario {
    @Column(unique = true)
    private String matricula;

    // Construtor CORRIGIDO: Agora passa a senha para o super
    public Funcionario(String cpf, String nome, String senha, String matricula) {
        super(cpf, nome, senha); // Chama o construtor de Usuario com senha
        this.matricula = matricula;
    }

    // Getter & Setter (Lombok @Data já gera, mas se quiser explícito, mantenha)
    // public String getMatricula() {
    // return matricula;
    // }

    // public void setMatricula(String matricula) {
    // this.matricula = matricula;
    // }
}