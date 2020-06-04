package com.anicare.project.service;

import com.anicare.project.model.Vet;
import com.anicare.project.repo.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;

    public VetService(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    public List<Vet> allVets() {
        return vetRepository.findAll();
    }
}
