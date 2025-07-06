package com.petshop.services;

import com.petshop.models.Funcionario;
import com.petshop.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Funcionario> findAllFuncionarios() {
        return funcionarioRepository.findAll();
    }

    public Optional<Funcionario> findFuncionarioByCpf(String cpf) {
        return funcionarioRepository.findById(cpf);
    }

    public Funcionario saveFuncionario(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public void deleteFuncionario(String cpf) {
        funcionarioRepository.deleteById(cpf);
    }
}
