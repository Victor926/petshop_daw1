package com.petshop.controller;

import com.petshop.models.Pet;
import com.petshop.services.PetService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cliente/{clienteCpf}/pets") // Adicionamos o CPF do cliente na URL para identificar o dono
public class PetClienteController {

    private final PetService petService;

    public PetClienteController(PetService petService) {
        this.petService = petService;
    }

    // LISTAR PETS DE UM CLIENTE
    @GetMapping
    public String listarPetsDoCliente(@PathVariable String clienteCpf, Model model) {
        List<Pet> pets = petService.findPetsByClienteCpf(clienteCpf);
        model.addAttribute("pets", pets);
        model.addAttribute("clienteCpf", clienteCpf);
        return "meus-pets";
    }

    // EXIBIR FORMULÁRIO PARA NOVO PET
    @GetMapping("/novo")
    public String exibirFormularioNovoPet(@PathVariable String clienteCpf, Model model) {
        model.addAttribute("pet", new Pet());
        model.addAttribute("clienteCpf", clienteCpf);
        return "pet-cadastro";
    }

    // EXIBIR FORMULÁRIO PARA EDITAR PET
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarPet(@PathVariable String clienteCpf, @PathVariable Long id, Model model) {
        Optional<Pet> pet = petService.findPetById(id);
        if (pet.isPresent()) {
            model.addAttribute("pet", pet.get());
            model.addAttribute("clienteCpf", clienteCpf);
            return "pet-cadastro";
        }
        return "redirect:/cliente/" + clienteCpf + "/pets";
    }

    // SALVAR/ATUALIZAR PET
    @PostMapping("/salvar")
    public String salvarPet(@PathVariable String clienteCpf, @ModelAttribute Pet pet, RedirectAttributes redirectAttributes) {
        try {
            petService.savePet(pet, clienteCpf);
            redirectAttributes.addFlashAttribute("mensagem", "Pet salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            if (pet.getId() != null) {
                return "redirect:/cliente/" + clienteCpf + "/pets/editar/" + pet.getId();
            }
            return "redirect:/cliente/" + clienteCpf + "/pets/novo";
        }
        return "redirect:/cliente/" + clienteCpf + "/pets";
    }

    // DELETAR PET
    @GetMapping("/deletar/{id}")
    public String deletarPet(@PathVariable String clienteCpf, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        petService.deletePet(id);
        redirectAttributes.addFlashAttribute("mensagem", "Pet excluído com sucesso!");
        return "redirect:/cliente/" + clienteCpf + "/pets";
    }
}