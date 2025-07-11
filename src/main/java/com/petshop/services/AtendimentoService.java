package com.petshop.services;

import com.petshop.models.Atendimento;
import com.petshop.models.Cliente;
import com.petshop.models.Funcionario;
import com.petshop.models.HorarioDisponivel;
import com.petshop.models.Pet;
import com.petshop.models.Servico;
import com.petshop.repositories.AtendimentoRepository;
import com.petshop.repositories.ClienteRepository;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.HorarioDisponivelRepository;
import com.petshop.repositories.PetRepository;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final HorarioDisponivelRepository horarioDisponivelRepository;
    private final ServicoRepository servicoRepository;

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                              ClienteRepository clienteRepository,
                              PetRepository petRepository,
                              FuncionarioRepository funcionarioRepository,
                              HorarioDisponivelRepository horarioDisponivelRepository,
                              ServicoRepository servicoRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.clienteRepository = clienteRepository;
        this.petRepository = petRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.horarioDisponivelRepository = horarioDisponivelRepository;
        this.servicoRepository = servicoRepository;
    }

    public List<Atendimento> findAllAtendimentos() {
        return atendimentoRepository.findAll();
    }

    public Optional<Atendimento> findAtendimentoById(Long id) {
        return atendimentoRepository.findById(id);
    }

    @Transactional
    public Atendimento saveAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    @Transactional
    public void deleteAtendimento(Long id) {
        atendimentoRepository.deleteById(id);
    }

    // Método para agendamento de atendimento
    @Transactional
    public Atendimento agendarAtendimento(
            String clienteCpf,
            Long petId,
            Long horarioDisponivelId,
            Long servicoId
            ) {

        Cliente cliente = clienteRepository.findById(clienteCpf)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado."));
        HorarioDisponivel horarioDisponivel = horarioDisponivelRepository.findById(horarioDisponivelId)
                .orElseThrow(() -> new IllegalArgumentException("Horário disponível não encontrado."));
        Servico servico = servicoRepository.findById(servicoId)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));

        // Verifica se o horário disponível está ocupado
        if (horarioDisponivel.getOcupado()) {
            throw new IllegalStateException("O horário selecionado já está ocupado.");
        }

        // Marca o horário como ocupado
        horarioDisponivel.setOcupado(true);
        horarioDisponivelRepository.save(horarioDisponivel);

        Atendimento atendimento = new Atendimento();
        atendimento.setCliente(cliente);
        atendimento.setPet(pet);
        atendimento.setFuncionario(horarioDisponivel.getFuncionario());
        atendimento.setServico(servico);
        atendimento.setDataHora(horarioDisponivel.getDataHoraInicio());
        atendimento.setStatus("AGENDADO");

        return atendimentoRepository.save(atendimento);
    }

    public List<Atendimento> findAtendimentosByCliente(String clienteCpf) {
        Cliente cliente = clienteRepository.findById(clienteCpf)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        return atendimentoRepository.findByClienteCpf(cliente.getCpf());
    }
}