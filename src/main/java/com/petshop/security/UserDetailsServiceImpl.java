package com.petshop.security;

import com.petshop.models.Usuario;
import com.petshop.models.Funcionario; // Importar Funcionario
import com.petshop.models.Cliente;     // Importar Cliente
import com.petshop.repositories.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.List; // Importar List

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String cpf) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findById(cpf)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o CPF: " + cpf));

        // Lógica de atribuição de papel baseada no tipo da instância
        String role;
        if (usuario instanceof Funcionario) {
            role = "ADMIN";
        } else if (usuario instanceof Cliente) {
            role = "CLIENTE";
        } else {
            role = "USER";
        }

        return new org.springframework.security.core.userdetails.User(
                usuario.getCpf(),
                usuario.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
    }
}