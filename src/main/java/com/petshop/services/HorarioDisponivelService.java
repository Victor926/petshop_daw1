package com.petshop.services;

import com.petshop.models.HorarioDisponivel;
import com.petshop.models.Funcionario;
import com.petshop.models.Servico;
import com.petshop.repositories.HorarioDisponivelRepository;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HorarioDisponivelService {

    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ServicoRepository servicoRepository;

    public HorarioDisponivelService(HorarioDisponivelRepository horarioDisponivelRepository,
                                    FuncionarioRepository funcionarioRepository,
                                    ServicoRepository servicoRepository) {
        this.horarioDisponivelRepository = horarioDisponivelRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.servicoRepository = servicoRepository;
    }

    // Método para salvar um novo HorarioDisponivel
    public HorarioDisponivel saveHorarioDisponivel(Long id, String funcionarioCpf, Long servicoId, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioCpf)
                                                 .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado."));

        Servico servico = null;
        if (servicoId != null) {
            servico = servicoRepository.findById(servicoId)
                                       .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));
        }

        // Validações básicas (você pode expandir isso)
        if (dataHoraInicio == null || dataHoraFim == null || dataHoraInicio.isAfter(dataHoraFim)) {
            throw new IllegalArgumentException("Período de horário inválido.");
        }
        if (dataHoraInicio.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Não é possível adicionar horários no passado.");
        }

        HorarioDisponivel horario;
        if (id == null) {
            horario = new HorarioDisponivel();
        } else {
            horario = horarioDisponivelRepository.findById(id)
                                                 .orElseThrow(() -> new IllegalArgumentException("Horário disponível não encontrado para edição."));
        }

        horario.setFuncionario(funcionario);
        horario.setServico(servico);
        horario.setDataHoraInicio(dataHoraInicio);
        horario.setDataHoraFim(dataHoraFim);
        horario.setOcupado(false); // Garante que, ao salvar/atualizar, ele esteja desocupado

        return horarioDisponivelRepository.save(horario);
    }

    // Método para buscar todos os horários disponíveis (para o admin listar)
    public List<HorarioDisponivel> findAllHorariosDisponiveis() {
        return horarioDisponivelRepository.findAll();
    }

    // Método para buscar um HorarioDisponivel pelo ID
    public Optional<HorarioDisponivel> findHorarioDisponivelById(Long id) {
        return horarioDisponivelRepository.findById(id);
    }

    // Método para buscar horários que ainda não estão ocupados (para o cliente ver)
    public List<HorarioDisponivel> findAvailableHorarios() {
        return horarioDisponivelRepository.findByOcupadoFalseOrderByDataHoraInicioAsc();
    }

    // Método para marcar um HorarioDisponivel como ocupado
    public HorarioDisponivel marcarComoOcupado(Long id) {
        HorarioDisponivel horario = horarioDisponivelRepository.findById(id)
                                                               .orElseThrow(() -> new IllegalArgumentException("Horário disponível não encontrado."));
        if (horario.getOcupado()) {
            throw new IllegalStateException("Horário já está ocupado.");
        }
        horario.setOcupado(true);
        return horarioDisponivelRepository.save(horario);
    }

    // Método para deletar um HorarioDisponivel
    public void deleteHorarioDisponivel(Long id) {
        horarioDisponivelRepository.deleteById(id);
    }
}
