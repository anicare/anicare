package com.anicare.project.service;

import com.anicare.project.model.Shelter;
import com.anicare.project.repo.ShelterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ShelterService {

    private final ShelterRepository shelterRepository;

    @Autowired
    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public List<Shelter> allShelters() {
        return shelterRepository.findAll();
    }
}
