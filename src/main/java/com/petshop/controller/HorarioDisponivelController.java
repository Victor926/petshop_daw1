package com.petshop.controller;

import com.petshop.models.HorarioDisponivel;
import com.petshop.models.Funcionario;
import com.petshop.models.Servico;
import com.petshop.services.HorarioDisponivelService;
import com.petshop.services.FuncionarioService;
import com.petshop.services.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/horarios")
public class HorarioDisponivelController {

    private final HorarioDisponivelService horarioDisponivelService;
    private final FuncionarioService funcionarioService;
    private final ServicoService servicoService;

    public HorarioDisponivelController(HorarioDisponivelService horarioDisponivelService,
                                       FuncionarioService funcionarioService,
                                       ServicoService servicoService) {
        this.horarioDisponivelService = horarioDisponivelService;
        this.funcionarioService = funcionarioService;
        this.servicoService = servicoService;
    }

    // LISTAR TODOS OS HORÁRIOS DISPONÍVEIS
    @GetMapping
    public String listarHorariosDisponiveis(Model model) {
        List<HorarioDisponivel> horarios = horarioDisponivelService.findAllHorariosDisponiveis();
        model.addAttribute("horarios", horarios);
        return "horarios-disponiveis";
    }

    // EXIBIR FORMULÁRIO PARA NOVO HORÁRIO DISPONÍVEL
    @GetMapping("/novo")
    public String exibirFormularioNovoHorario(Model model) {
        model.addAttribute("horario", new HorarioDisponivel());
        model.addAttribute("funcionarios", funcionarioService.findAllFuncionarios()); 
        model.addAttribute("servicos", servicoService.findAllServicos()); 
        return "horario-disponivel-form"; 
    }

    // EXIBIR FORMULÁRIO PARA EDITAR HORÁRIO DISPONÍVEL
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarHorario(@PathVariable Long id, Model model) {
        Optional<HorarioDisponivel> horario = horarioDisponivelService.findHorarioDisponivelById(id);
        if (horario.isPresent()) {
            model.addAttribute("horario", horario.get());
            model.addAttribute("funcionarios", funcionarioService.findAllFuncionarios());
            model.addAttribute("servicos", servicoService.findAllServicos());
            return "horario-disponivel-form";
        }
        return "redirect:/admin/horarios";
    }

    // SALVAR/ATUALIZAR HORÁRIO DISPONÍVEL
    @PostMapping("/salvar")
    public String salvarHorario(
            @ModelAttribute("horario") HorarioDisponivel horario, 
            RedirectAttributes redirectAttributes) {

        try {
            String funcionarioCpf = horario.getFuncionario() != null ? horario.getFuncionario().getCpf() : null;
            Long servicoId = horario.getServico() != null ? horario.getServico().getId() : null;
            LocalDateTime dataHoraInicio = horario.getDataHoraInicio();
            LocalDateTime dataHoraFim = horario.getDataHoraFim();

            if (funcionarioCpf == null || funcionarioCpf.isEmpty()) {
                throw new IllegalArgumentException("Funcionário é obrigatório.");
            }
            if (dataHoraInicio == null) {
                throw new IllegalArgumentException("Data e hora de início são obrigatórias.");
            }
            if (dataHoraFim == null) {
                throw new IllegalArgumentException("Data e hora de fim são obrigatórias.");
            }


            horarioDisponivelService.saveHorarioDisponivel(
                    horario.getId(),
                    funcionarioCpf,
                    servicoId,
                    dataHoraInicio,
                    dataHoraFim
            );
            redirectAttributes.addFlashAttribute("mensagem", "Horário disponível salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar horário: " + e.getMessage());
            if (horario.getId() != null) {
                return "redirect:/admin/horarios/editar/" + horario.getId();
            }
            return "redirect:/admin/horarios/novo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro inesperado ao salvar horário: " + e.getMessage());
            if (horario.getId() != null) {
                return "redirect:/admin/horarios/editar/" + horario.getId();
            }
            return "redirect:/admin/horarios/novo";
        }
        return "redirect:/admin/horarios";
    }

    // DELETAR HORÁRIO DISPONÍVEL
    @GetMapping("/deletar/{id}")
    public String deletarHorario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            horarioDisponivelService.deleteHorarioDisponivel(id);
            redirectAttributes.addFlashAttribute("mensagem", "Horário disponível excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir horário: " + e.getMessage());
        }
        return "redirect:/admin/horarios";
    }
}