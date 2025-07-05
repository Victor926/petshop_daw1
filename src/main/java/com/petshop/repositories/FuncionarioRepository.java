package com.petshop.repositories;

import com.petshop.models.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, String> { // ID de Funcionario é String (CPF)
}