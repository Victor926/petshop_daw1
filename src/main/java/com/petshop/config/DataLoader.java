package com.petshop.config; // Ou com.petshop.util;

import com.petshop.models.Funcionario; // Importar Funcionario
import com.petshop.repositories.FuncionarioRepository; // Importar FuncionarioRepository
import com.petshop.repositories.UsuarioRepository; // Importar UsuarioRepository
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    private final UsuarioRepository usuarioRepository;
    private final FuncionarioRepository funcionarioRepository; // Para associar o admin a um funcionário
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UsuarioRepository usuarioRepository, FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            // Verifica se o usuário 'admin' já existe (usando um CPF fixo para o admin)
            String adminCpf = "10688847625"; // CPF para o usuário admin
            if (usuarioRepository.findById(adminCpf).isEmpty()) {
                System.out.println("Criando usuário administrador inicial...");

                // Cria o usuário (que também é um funcionário)
                Funcionario adminFuncionario = new Funcionario();
                adminFuncionario.setCpf(adminCpf);
                adminFuncionario.setNome("Administrador Principal");
                adminFuncionario.setSenha(passwordEncoder.encode("admin123")); // Senha: admin123
                adminFuncionario.setMatricula("ADM001"); // Matrícula para o funcionário

                // Salva o funcionário (que o JPA salvará como Usuario e Funcionario devido à herança JOINED)
                funcionarioRepository.save(adminFuncionario);

                System.out.println("Usuário administrador '"+ adminCpf +"' criado com sucesso!");
            } else {
                System.out.println("Usuário administrador já existe.");
            }
        };
    }
}