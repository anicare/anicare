package com.anicare.project.service;

import com.anicare.project.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DetailsService implements UserDetailsService {

    private final CustomerService customerService;

    @Autowired
    public DetailsService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Customer customer = customerService.findByUsername(username);
        if (customer != null)
            return new User(customer.getUsername(), customer.getPassword(), customer.isEnabled(), true, true, true, customer.getAuthorities());
        else throw new UsernameNotFoundException("No user was found with username " + username);
    }
}