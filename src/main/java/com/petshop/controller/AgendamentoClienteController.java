package com.petshop.controller;

import com.petshop.models.Atendimento;
import com.petshop.models.Cliente;
import com.petshop.models.HorarioDisponivel;
import com.petshop.models.Pet;
import com.petshop.models.Servico;
import com.petshop.services.AtendimentoService;
import com.petshop.services.ClienteService;
import com.petshop.services.HorarioDisponivelService;
import com.petshop.services.PetService;
import com.petshop.services.ServicoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente")
public class AgendamentoClienteController {

    private final AtendimentoService atendimentoService;
    private final ClienteService clienteService;
    private final PetService petService;
    private final HorarioDisponivelService horarioDisponivelService;
    private final ServicoService servicoService;

    public AgendamentoClienteController(AtendimentoService atendimentoService,
                                        ClienteService clienteService,
                                        PetService petService,
                                        HorarioDisponivelService horarioDisponivelService,
                                        ServicoService servicoService) {
        this.atendimentoService = atendimentoService;
        this.clienteService = clienteService;
        this.petService = petService;
        this.horarioDisponivelService = horarioDisponivelService;
        this.servicoService = servicoService;
    }

    // Exibir formulário de agendamento com todos os horários disponíveis
    @GetMapping("/{clienteCpf}/agendamentos/novo")
    public String exibirFormularioAgendamento(
            @PathVariable String clienteCpf,
            Model model, RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!username.equals(clienteCpf)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado.");
            return "redirect:/login";
        }

        Optional<Cliente> clienteOptional = clienteService.findClienteByCpf(username);
        if (clienteOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("erro", "Cliente não encontrado.");
            return "redirect:/login";
        }
        Cliente cliente = clienteOptional.get();

        // Carrega horários disponíveis que NÃO estão ocupados, são no futuro E TÊM UM SERVIÇO ASSOCIADO
        List<HorarioDisponivel> horariosDisponiveis = horarioDisponivelService.findAllHorariosDisponiveis().stream()
                .filter(h -> !h.getOcupado() && h.getDataHoraInicio().isAfter(LocalDateTime.now()) && h.getServico() != null) // Adicionado filtro: h.getServico() != null
                .collect(Collectors.toList());

        model.addAttribute("cliente", cliente);
        model.addAttribute("clienteCpf", username);
        model.addAttribute("pets", petService.findPetsByClienteCpf(username));
        model.addAttribute("servicos", servicoService.findAllServicos());
        model.addAttribute("horariosDisponiveis", horariosDisponiveis);
        model.addAttribute("atendimento", new Atendimento());

        return "agendar-servico";
    }

    @PostMapping("/{clienteCpf}/agendamentos/salvar")
    public String salvarAgendamento(
            @ModelAttribute("atendimento") Atendimento atendimento,
            @RequestParam("horarioDisponivelId") Long horarioDisponivelId,
            @RequestParam("clienteCpf") String clienteCpf,
            @RequestParam("petId") Long petId,
            RedirectAttributes redirectAttributes) {

        try {
            if (horarioDisponivelId == null) {
                throw new IllegalArgumentException("Por favor, selecione um horário disponível.");
            }
            if (petId == null) {
                throw new IllegalArgumentException("Por favor, selecione um pet.");
            }

            Optional<HorarioDisponivel> horarioOpt = horarioDisponivelService.findHorarioDisponivelById(horarioDisponivelId);
            if (horarioOpt.isEmpty()) {
                throw new IllegalArgumentException("Horário disponível não encontrado.");
            }
            HorarioDisponivel horarioSelecionado = horarioOpt.get();
            
            // finalServicoId nunca será nulo aqui, pois a lista de horários já foi filtrada
            Long finalServicoId = horarioSelecionado.getServico().getId(); 

            atendimentoService.agendarAtendimento(
                    clienteCpf,
                    petId,
                    horarioDisponivelId,
                    finalServicoId
            );
            redirectAttributes.addFlashAttribute("mensagem", "Agendamento realizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao agendar: " + e.getMessage());
            return "redirect:/cliente/" + clienteCpf + "/agendamentos/novo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao agendar: " + e.getMessage());
            return "redirect:/cliente/" + clienteCpf + "/agendamentos/novo";
        }
        return "redirect:/cliente/" + clienteCpf + "/agendamentos";
    }

    // Método para listar os agendamentos do cliente
    @GetMapping("/{clienteCpf}/agendamentos")
    public String meusAgendamentos(@PathVariable String clienteCpf, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInCpf = authentication.getName();

        if (!loggedInCpf.equals(clienteCpf)) {
            return "redirect:/acesso-negado";
        }

        try {
            List<Atendimento> atendimentos = atendimentoService.findAtendimentosByCliente(clienteCpf);
            model.addAttribute("atendimentos", atendimentos);
            model.addAttribute("clienteCpf", clienteCpf);
            return "meus-agendamentos";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "meus-agendamentos";
        }
    }

    // Método para cancelar agendamento
    @GetMapping("/{clienteCpf}/agendamentos/cancelar/{id}")
    public String cancelarAgendamento(@PathVariable String clienteCpf, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInCpf = authentication.getName();

        if (!loggedInCpf.equals(clienteCpf)) {
            redirectAttributes.addFlashAttribute("erro", "Acesso negado para cancelar agendamentos de outro cliente.");
            return "redirect:/cliente/" + loggedInCpf + "/agendamentos";
        }

        try {
            Optional<Atendimento> atendimentoOpt = atendimentoService.findAtendimentoById(id);
            if (atendimentoOpt.isPresent()) {
                Atendimento atendimento = atendimentoOpt.get();
                if (!atendimento.getCliente().getCpf().equals(clienteCpf)) {
                    redirectAttributes.addFlashAttribute("erro", "Você não tem permissão para cancelar este agendamento.");
                    return "redirect:/cliente/" + clienteCpf + "/agendamentos";
                }
                
                atendimento.setStatus("CANCELADO");
                atendimentoService.saveAtendimento(atendimento);

                String funcionarioCpf = atendimento.getFuncionario().getCpf();
                LocalDateTime dataHoraAtendimento = atendimento.getDataHora();
                Long servicoId = (atendimento.getServico() != null) ? atendimento.getServico().getId() : null;

                Optional<HorarioDisponivel> horarioDisponivelOpt = horarioDisponivelService.findHorarioDisponivelParaLiberar(
                    funcionarioCpf,
                    dataHoraAtendimento,
                    servicoId
                );

                if (horarioDisponivelOpt.isPresent()) {
                    HorarioDisponivel horarioDisponivel = horarioDisponivelOpt.get();
                    horarioDisponivel.setOcupado(false);
                    horarioDisponivelService.saveHorarioDisponivel(
                        horarioDisponivel.getId(),
                        horarioDisponivel.getFuncionario().getCpf(),
                        horarioDisponivel.getServico() != null ? horarioDisponivel.getServico().getId() : null,
                        horarioDisponivel.getDataHoraInicio(),
                        horarioDisponivel.getDataHoraFim()
                    );
                } else {
                    System.out.println("Aviso: Horário Disponível correspondente não encontrado para o atendimento ID: " + id);
                }

                redirectAttributes.addFlashAttribute("mensagem", "Agendamento cancelado com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Agendamento não encontrado.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar agendamento: " + e.getMessage());
        }
        return "redirect:/cliente/" + clienteCpf + "/agendamentos";
    }
}