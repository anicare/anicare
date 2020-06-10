package com.anicare.project.service;

import com.anicare.project.model.Customer;
import com.anicare.project.model.Vet;
import com.anicare.project.repo.VetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VetService {

    private final VetRepository vetRepository;

    public VetService(VetRepository vetRepository) {
        this.vetRepository = vetRepository;
    }

    public List<Customer> allVets() {
        return vetRepository.findAll()
                .stream().map(Vet::getCustomer)
                .collect(Collectors.toList());
    }
}
