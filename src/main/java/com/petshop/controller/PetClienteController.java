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
    // Ex: GET /cliente/12345678900/pets
    @GetMapping
    public String listarPetsDoCliente(@PathVariable String clienteCpf, Model model) {
        List<Pet> pets = petService.findPetsByClienteCpf(clienteCpf);
        model.addAttribute("pets", pets);
        model.addAttribute("clienteCpf", clienteCpf); // Para usar nas URLs dos formulários
        return "meus-pets"; // Nome do arquivo HTML para exibir a lista de pets
    }

    // EXIBIR FORMULÁRIO PARA NOVO PET
    // Ex: GET /cliente/12345678900/pets/novo
    @GetMapping("/novo")
    public String exibirFormularioNovoPet(@PathVariable String clienteCpf, Model model) {
        model.addAttribute("pet", new Pet()); // Objeto Pet vazio para o formulário
        model.addAttribute("clienteCpf", clienteCpf);
        return "pet-cadastro"; // Nome do arquivo HTML do formulário de cadastro/edição
    }

    // EXIBIR FORMULÁRIO PARA EDITAR PET
    // Ex: GET /cliente/12345678900/pets/editar/{id}
    @GetMapping("/editar/{id}")
    public String exibirFormularioEditarPet(@PathVariable String clienteCpf, @PathVariable Long id, Model model) {
        Optional<Pet> pet = petService.findPetById(id);
        if (pet.isPresent()) {
            model.addAttribute("pet", pet.get());
            model.addAttribute("clienteCpf", clienteCpf);
            return "pet-cadastro"; // Reusa o mesmo formulário
        }
        // Redireciona para a lista se o pet não for encontrado
        return "redirect:/cliente/" + clienteCpf + "/pets";
    }

    // SALVAR/ATUALIZAR PET (via POST)
    // Ex: POST /cliente/12345678900/pets/salvar
    @PostMapping("/salvar")
    public String salvarPet(@PathVariable String clienteCpf, @ModelAttribute Pet pet, RedirectAttributes redirectAttributes) {
        try {
            petService.savePet(pet, clienteCpf);
            redirectAttributes.addFlashAttribute("mensagem", "Pet salvo com sucesso!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            // Se for uma edição e o ID existir, redireciona de volta ao formulário de edição
            if (pet.getId() != null) {
                return "redirect:/cliente/" + clienteCpf + "/pets/editar/" + pet.getId();
            }
            // Caso contrário, redireciona para o formulário de novo pet
            return "redirect:/cliente/" + clienteCpf + "/pets/novo";
        }
        return "redirect:/cliente/" + clienteCpf + "/pets"; // Redireciona para a lista de pets após salvar
    }

    // DELETAR PET
    // Ex: GET /cliente/12345678900/pets/deletar/{id} (Geralmente DELETE, mas GET para simplicidade inicial)
    @GetMapping("/deletar/{id}")
    public String deletarPet(@PathVariable String clienteCpf, @PathVariable Long id, RedirectAttributes redirectAttributes) {
        petService.deletePet(id);
        redirectAttributes.addFlashAttribute("mensagem", "Pet excluído com sucesso!");
        return "redirect:/cliente/" + clienteCpf + "/pets";
    }
}