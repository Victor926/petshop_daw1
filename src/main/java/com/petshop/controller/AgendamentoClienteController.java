package com.petshop.controller;

import com.petshop.models.Atendimento;
import com.petshop.models.Pet;
// REMOVER: import com.petshop.models.Funcionario; // Não mais necessário se só usar HorarioDisponivel
// REMOVER: import com.petshop.models.Servico;     // Não mais necessário se só usar HorarioDisponivel
import com.petshop.models.HorarioDisponivel; // NOVO: Importar HorarioDisponivel
import com.petshop.services.AtendimentoService;
import com.petshop.services.PetService;
import com.petshop.services.HorarioDisponivelService; // NOVO: Importar HorarioDisponivelService
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime; // Manter se for usado para DateTimeParseException
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente/{clienteCpf}/agendamentos") // URL base para agendamentos do cliente
public class AgendamentoClienteController {

    private final AtendimentoService atendimentoService;
    private final PetService petService;
    private final HorarioDisponivelService horarioDisponivelService; // NOVO: Injetar HorarioDisponivelService

    // CONSTRUTOR ATUALIZADO
    public AgendamentoClienteController(AtendimentoService atendimentoService,
                                        PetService petService,
                                        HorarioDisponivelService horarioDisponivelService) { // NOVO: Adicionar ao construtor
        this.atendimentoService = atendimentoService;
        this.petService = petService;
        this.horarioDisponivelService = horarioDisponivelService; // NOVO: Atribuir
    }

    // EXIBIR FORMULÁRIO DE AGENDAMENTO (AGORA COM HORÁRIOS DISPONÍVEIS)
    @GetMapping("/novo")
    public String exibirFormularioAgendamento(@PathVariable String clienteCpf, Model model) {
        List<Pet> petsDoCliente = petService.findPetsByClienteCpf(clienteCpf);
        if (petsDoCliente.isEmpty()) {
            model.addAttribute("erro", "Você precisa cadastrar um pet antes de agendar um serviço!");
            return "redirect:/cliente/" + clienteCpf + "/pets/novo"; // Redireciona para o form de cadastro de pet
        }

        // Carrega APENAS os horários disponíveis (não ocupados)
        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelService.findAvailableHorarios();
        if (horariosDisponiveis.isEmpty()) {
            model.addAttribute("erro", "No momento, não há horários disponíveis para agendamento. Por favor, tente novamente mais tarde.");
            // Pode redirecionar para uma página de erro ou manter na home
            return "index"; // Ou uma página de "sem_horarios"
        }

        model.addAttribute("atendimento", new Atendimento()); // Objeto Atendimento vazio para o formulário
        model.addAttribute("clienteCpf", clienteCpf);
        model.addAttribute("pets", petsDoCliente);
        model.addAttribute("horariosDisponiveis", horariosDisponiveis); // NOVO: Passar os horários disponíveis para a view
        
        // REMOVIDOS: Funcionarios e Servicos não são mais necessários individualmente no model para esta funcionalidade
        // model.addAttribute("funcionarios", atendimentoService.findAllFuncionarios());
        // model.addAttribute("servicos", atendimentoService.findAllServicos());
        
        return "agendar-servico"; // Nome do arquivo HTML do formulário de agendamento
    }

    // SALVAR NOVO AGENDAMENTO (AGORA COM SELEÇÃO DE HORÁRIO DISPONÍVEL)
    @PostMapping("/salvar")
    public String salvarAgendamento(
            @PathVariable String clienteCpf,
            @RequestParam("petId") Long petId,
            @RequestParam("horarioDisponivelId") Long horarioDisponivelId, // NOVO: Recebe o ID do HorarioDisponivel
            RedirectAttributes redirectAttributes) {

        try {
            // A chamada ao service AGORA só tem 3 argumentos
            Atendimento novoAtendimento = atendimentoService.agendarAtendimento(
                    clienteCpf, petId, horarioDisponivelId 
            );
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento realizado com sucesso! ID: " + novoAtendimento.getId());
        } catch (IllegalArgumentException | DateTimeParseException | IllegalStateException e) { // NOVO: Adicionar IllegalStateException
            redirectAttributes.addFlashAttribute("erro", "Erro ao agendar: " + e.getMessage());
            return "redirect:/cliente/" + clienteCpf + "/agendamentos/novo"; // Volta para o formulário
        }
        return "redirect:/cliente/" + clienteCpf + "/agendamentos"; // Redireciona para a lista de agendamentos
    }

    // LISTAR AGENDAMENTOS DE UM CLIENTE (MANTÉM O MESMO)
    @GetMapping
    public String listarAgendamentosDoCliente(@PathVariable String clienteCpf, Model model) {
        List<Atendimento> atendimentos = atendimentoService.findAtendimentosByClienteCpf(clienteCpf);
        model.addAttribute("atendimentos", atendimentos);
        model.addAttribute("clienteCpf", clienteCpf);
        return "meus-agendamentos"; // Nome do arquivo HTML para exibir a lista de agendamentos
    }

    // CANCELAR AGENDAMENTO (MANTÉM O MESMO, MAS O SERVICE PODE DESOCUPAR O HORÁRIO)
    @GetMapping("/cancelar/{id}")
    public String cancelarAgendamento(@PathVariable String clienteCpf, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            atendimentoService.cancelarAtendimento(id);
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento " + id + " cancelado com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cliente/" + clienteCpf + "/agendamentos";
    }
}