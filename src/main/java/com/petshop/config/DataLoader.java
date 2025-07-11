package com.petshop.config;

import com.petshop.models.Funcionario;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            String adminCpf = "10688847625";
            if (usuarioRepository.findById(adminCpf).isEmpty()) {
                System.out.println("Criando usuário administrador inicial...");


                Funcionario adminFuncionario = new Funcionario();
                adminFuncionario.setCpf(adminCpf);
                adminFuncionario.setNome("Administrador Principal");
                adminFuncionario.setSenha(passwordEncoder.encode("admin123"));
                adminFuncionario.setMatricula("ADM001");

                funcionarioRepository.save(adminFuncionario);

                System.out.println("Usuário administrador '"+ adminCpf +"' criado com sucesso!");
            } else {
                System.out.println("Usuário administrador já existe.");
            }
        };
    }
}