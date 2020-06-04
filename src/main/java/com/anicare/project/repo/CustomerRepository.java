package com.anicare.project.repo;

import com.anicare.project.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUsername(String username);

    Customer findByEmail(String email);
}
