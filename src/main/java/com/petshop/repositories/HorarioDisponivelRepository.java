package com.petshop.repositories;

import com.petshop.models.HorarioDisponivel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDateTime;

public interface HorarioDisponivelRepository extends JpaRepository<HorarioDisponivel, Long> {

    // Exemplo de método de busca para encontrar horários disponíveis para um funcionário em um período
    List<HorarioDisponivel> findByFuncionarioCpfAndDataHoraInicioBetweenOrderByDataHoraInicioAsc(String funcionarioCpf, LocalDateTime start, LocalDateTime end);

    // Método para encontrar horários disponíveis que não estejam ocupados, ordenados por data e hora
    List<HorarioDisponivel> findByOcupadoFalseOrderByDataHoraInicioAsc();

    // Método para encontrar horários disponíveis para um serviço específico
    List<HorarioDisponivel> findByServicoIdAndOcupadoFalseOrderByDataHoraInicioAsc(Long servicoId);

    // Método para encontrar horários disponíveis por funcionário
    List<HorarioDisponivel> findByFuncionarioCpfAndOcupadoFalseOrderByDataHoraInicioAsc(String funcionarioCpf);

    List<HorarioDisponivel> findByFuncionarioCpfAndDataHoraInicio(String funcionarioCpf, LocalDateTime dataHoraInicio);
}