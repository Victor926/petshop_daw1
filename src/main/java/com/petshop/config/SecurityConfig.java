package com.petshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Define o PasswordEncoder que será usado para criptografar senhas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuração inicial de usuários em memória (para testes)
    // No futuro, isso será substituído por um UserDetailsService que busca usuários do banco de dados
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.withUsername("admin")
                                .password(passwordEncoder.encode("admin123")) // Senha criptografada
                                .roles("ADMIN")
                                .build();
        UserDetails cliente = User.withUsername("cliente")
                                  .password(passwordEncoder.encode("cliente123")) // Senha criptografada
                                  .roles("CLIENTE")
                                  .build();
        return new InMemoryUserDetailsManager(admin, cliente);
    }

    // Configuração das regras de segurança HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Páginas públicas que não exigem autenticação
                .requestMatchers("/", "/index.html", "/login/**", "/cadastro/**", "/css/**", "/js/**").permitAll()
                // Regras de acesso para perfis (ROLES)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/cliente/**").hasRole("CLIENTE")
                // Qualquer outra requisição exige autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada (que vamos criar)
                .defaultSuccessUrl("/default-dashboard", true) // URL de sucesso padrão após login
                .permitAll() // Permite acesso à página de login para todos
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // URL para logout
                .logoutSuccessUrl("/") // URL após logout bem-sucedido
                .permitAll() // Permite acesso à URL de logout para todos
            );
            // .csrf(csrf -> csrf.disable()); // Apenas desabilite CSRF se souber o que está fazendo, para APIs REST por exemplo

        return http.build();
    }
}
