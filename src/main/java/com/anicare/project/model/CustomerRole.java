package com.anicare.project.model;

import javax.persistence.*;

@Entity
@Table(name = "customer_role")
public class CustomerRole {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long customerRoleId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    public CustomerRole() {
        super();
    }

    public CustomerRole(Customer customer, Role role) {
        super();
        this.customer = customer;
        this.role = role;
    }

    public Long getCustomerRoleId() {
        return customerRoleId;
    }

    public void setCustomerRoleId(Long customerRoleId) {
        this.customerRoleId = customerRoleId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
