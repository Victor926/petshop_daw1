package com.petshop.repositories;

import com.petshop.models.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // Importar List

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
    List<Atendimento> findByClienteCpf(String cpf);
}