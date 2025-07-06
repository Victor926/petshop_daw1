package com.petshop.repositories;

import com.petshop.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> { // O ID de Usuario é String (CPF)
    Optional<Usuario> findByCpf(String cpf); // Método para buscar usuário pelo CPF
}
