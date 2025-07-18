package com.petshop.services;

import com.petshop.models.Funcionario;
import com.petshop.models.HorarioDisponivel;
import com.petshop.models.Servico;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.HorarioDisponivelRepository;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<HorarioDisponivel> findAllHorariosDisponiveis() {
        return horarioDisponivelRepository.findAll();
    }

    public Optional<HorarioDisponivel> findHorarioDisponivelById(Long id) {
        return horarioDisponivelRepository.findById(id);
    }

    @Transactional
    public HorarioDisponivel saveHorarioDisponivel(Long id, String funcionarioCpf, Long servicoId, LocalDateTime dataHoraInicio, LocalDateTime dataHoraFim) {
        Funcionario funcionario = funcionarioRepository.findById(funcionarioCpf)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com o CPF: " + funcionarioCpf));

        Servico servico = null;
        if (servicoId != null) {
            servico = servicoRepository.findById(servicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado com o ID: " + servicoId));
        }

        HorarioDisponivel horario = new HorarioDisponivel();
        if (id != null) {
            horario = horarioDisponivelRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Horário disponível não encontrado com o ID: " + id));
        }

        horario.setFuncionario(funcionario);
        horario.setServico(servico);
        horario.setDataHoraInicio(dataHoraInicio);
        horario.setDataHoraFim(dataHoraFim);
        horario.setOcupado(false);

        return horarioDisponivelRepository.save(horario);
    }

    @Transactional
    public void deleteHorarioDisponivel(Long id) {
        horarioDisponivelRepository.deleteById(id);
    }

    public Optional<HorarioDisponivel> findHorarioDisponivelParaLiberar(String funcionarioCpf, LocalDateTime dataHoraInicio, Long servicoId) {
        List<HorarioDisponivel> horariosEncontrados = horarioDisponivelRepository.findByFuncionarioCpfAndDataHoraInicio(funcionarioCpf, dataHoraInicio);

        if (horariosEncontrados.isEmpty()) {
            return Optional.empty();
        }

        
        for (HorarioDisponivel horario : horariosEncontrados) {
            if (servicoId != null) {
                if ((horario.getServico() != null && horario.getServico().getId().equals(servicoId)) || horario.getServico() == null) {
                    return Optional.of(horario);
                }
            } else {
                if (horario.getServico() == null) {
                    return Optional.of(horario);
                }
            }
        }
        if(horariosEncontrados.size() == 1 && servicoId == null && horariosEncontrados.get(0).getServico() == null) {
             return Optional.of(horariosEncontrados.get(0));
        }
        return Optional.empty();
    }
}