package com.petshop.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.JOINED)
@Data // Inclui getters, setters, equals, hashCode, toString
@NoArgsConstructor
@AllArgsConstructor // Adicionar se você quiser um construtor com todos os campos
public abstract class Usuario { // Permanece abstrata

    @Id
    private String cpf;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false) // Senha não pode ser nula
    private String senha; // NOVO: Campo para armazenar a senha

    // Se você não estiver usando Lombok, adicione manualmente:
    // public String getSenha() { return senha; }
    // public void setSenha(String senha) { this.senha = senha; }
}