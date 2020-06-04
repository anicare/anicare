package com.anicare.project.service;

import com.anicare.project.model.Customer;
import com.anicare.project.model.Pet;
import com.anicare.project.repo.PetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    public List<Pet> petsByCustomer(Customer customer) {
        return petRepository.findByCustomer(customer);
    }

    public Pet getOne(Long id) {
        return petRepository.findOne(id);
    }

    public void savePet(Pet pet) {
        petRepository.save(pet);
    }
}
