package com.petshop.services;

import com.petshop.models.Servico;
import com.petshop.repositories.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> findAllServicos() {
        return servicoRepository.findAll();
    }

    public Optional<Servico> findServicoById(Long id) {
        return servicoRepository.findById(id);
    }

    public Servico saveServico(Servico servico) {
        return servicoRepository.save(servico);
    }

    public void deleteServico(Long id) {
        servicoRepository.deleteById(id);
    }
}