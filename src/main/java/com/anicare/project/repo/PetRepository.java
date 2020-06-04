package com.anicare.project.repo;

import com.anicare.project.model.Customer;
import com.anicare.project.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByCustomer(Customer customer);
}
