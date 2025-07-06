package com.petshop.services;

import com.petshop.models.Atendimento;
import com.petshop.models.Cliente;
import com.petshop.models.Pet;
import com.petshop.models.Funcionario;
import com.petshop.models.Servico;
import com.petshop.models.HorarioDisponivel; // Importar HorarioDisponivel
import com.petshop.repositories.AtendimentoRepository;
import com.petshop.repositories.ClienteRepository;
import com.petshop.repositories.PetRepository;
import com.petshop.repositories.FuncionarioRepository;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final ClienteRepository clienteRepository;
    private final PetRepository petRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ServicoRepository servicoRepository;
    private final HorarioDisponivelService horarioDisponivelService; // Nova dependência

    public AtendimentoService(AtendimentoRepository atendimentoRepository,
                              ClienteRepository clienteRepository,
                              PetRepository petRepository,
                              FuncionarioRepository funcionarioRepository,
                              ServicoRepository servicoRepository,
                              HorarioDisponivelService horarioDisponivelService) { // Adicionar ao construtor
        this.atendimentoRepository = atendimentoRepository;
        this.clienteRepository = clienteRepository;
        this.petRepository = petRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.servicoRepository = servicoRepository;
        this.horarioDisponivelService = horarioDisponivelService; // Atribuir
    }

    // MÉTODO AGENDARATENDIMENTO ATUALIZADO PARA USAR HORARIODISPONIVELID
    public Atendimento agendarAtendimento(String clienteCpf, Long petId, Long horarioDisponivelId) {
        // Buscar o HorarioDisponivel
        HorarioDisponivel horarioSelecionado = horarioDisponivelService.findHorarioDisponivelById(horarioDisponivelId)
                                                                       .orElseThrow(() -> new IllegalArgumentException("Horário disponível selecionado não encontrado."));

        // Verificar se o horário já está ocupado
        if (horarioSelecionado.getOcupado()) {
            throw new IllegalStateException("O horário selecionado já está ocupado. Por favor, escolha outro.");
        }

        // Buscar as outras entidades
        Cliente cliente = clienteRepository.findById(clienteCpf)
                                           .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado."));
        Pet pet = petRepository.findById(petId)
                               .orElseThrow(() -> new IllegalArgumentException("Pet não encontrado."));
        
        // As informações de Funcionario, Servico e DataHora vêm do HorarioDisponivel
        Funcionario funcionario = horarioSelecionado.getFuncionario();
        Servico servico = horarioSelecionado.getServico();
        LocalDateTime dataHora = horarioSelecionado.getDataHoraInicio(); // Usar o início do slot como dataHora do atendimento

        // Verificar se o pet pertence ao cliente
        if (!pet.getDono().getCpf().equals(clienteCpf)) {
            throw new IllegalArgumentException("O pet selecionado não pertence a este cliente.");
        }

        // Criar o Atendimento
        Atendimento atendimento = new Atendimento();
        atendimento.setCliente(cliente);
        atendimento.setPet(pet);
        atendimento.setFuncionario(funcionario);
        atendimento.setServico(servico);
        atendimento.setDataHora(dataHora);
        atendimento.setStatus("AGENDADO");
        atendimento.setConfirmado(false);

        // Salvar o Atendimento
        Atendimento savedAtendimento = atendimentoRepository.save(atendimento);

        // Marcar o HorarioDisponivel como ocupado APÓS o atendimento ser salvo com sucesso
        horarioDisponivelService.marcarComoOcupado(horarioSelecionado.getId());

        return savedAtendimento;
    }

    // O restante da classe (findAtendimentosByClienteCpf, findAtendimentoById, cancelarAtendimento, etc.) permanece o mesmo.
    // ...
    // Método para buscar todos os agendamentos de um cliente (MANTÉM O MESMO)
    public List<Atendimento> findAtendimentosByClienteCpf(String clienteCpf) {
        return atendimentoRepository.findByClienteCpf(clienteCpf);
    }

    // Método para buscar um atendimento pelo ID (MANTÉM O MESMO)
    public Optional<Atendimento> findAtendimentoById(Long id) {
        return atendimentoRepository.findById(id);
    }

    // Método para cancelar um atendimento (AGORA PODE DESOCUPAR O HORÁRIO)
    public Atendimento cancelarAtendimento(Long id) {
        Atendimento atendimento = atendimentoRepository.findById(id)
                                                       .orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado."));
        
        if ("CANCELADO".equals(atendimento.getStatus())) {
            throw new IllegalStateException("Este agendamento já está cancelado.");
        }

        atendimento.setStatus("CANCELADO");
        Atendimento updatedAtendimento = atendimentoRepository.save(atendimento);

        return updatedAtendimento;
    }

    // Método para atualizar um atendimento existente (MANTÉM O MESMO)
    public Atendimento updateAtendimento(Atendimento atendimento) {
        return atendimentoRepository.save(atendimento);
    }

    // Métodos para listar todos os funcionários e serviços (MANTÉM O MESMO)
    public List<Funcionario> findAllFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public List<Servico> findAllServicos() {
        return servicoRepository.findAll();
    }
}