package com.anicare.project.service;

import com.anicare.project.model.Customer;
import com.anicare.project.model.CustomerRole;
import com.anicare.project.repo.CustomerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomerService(CustomerRepository customerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public void updateCustomer(final Customer customer) {
        customerRepository.save(customer);
    }

    public void saveCustomer(final Customer customer, Set<CustomerRole> customerRoles) {
        customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        customer.getCustomerRoles().addAll(customerRoles);
        customerRepository.save(customer);
    }

    public boolean checkUsernameExist(String username) {
        return null != findByUsername(username);
    }

    public boolean checkEmailExist(String email) {
        return null != findByEmail(email);
    }

    public boolean checkUserExist(String username, String email) {
        return checkUsernameExist(username) || checkEmailExist(email);
    }
}
