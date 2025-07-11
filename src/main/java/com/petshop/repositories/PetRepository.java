package com.petshop.repositories;

import com.petshop.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Importar

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    @Transactional
    void deleteByDonoCpf(String donoCpf);
}