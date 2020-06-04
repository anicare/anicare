package com.anicare.project.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Role {

    @Id
    private int roleId = 2;
    private String name;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CustomerRole> customerRoles = new HashSet<>();

    public Role() {
        super();
    }

    public Role(String name, Set<CustomerRole> customerRoles) {
        super();
        this.name = name;
        this.customerRoles = customerRoles;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CustomerRole> getCustomerRoles() {
        return customerRoles;
    }

    public void setCustomerRoles(Set<CustomerRole> customerRoles) {
        this.customerRoles = customerRoles;
    }

    public Role getDefaultRole() {
        Role role = new Role();
        role.setRoleId(2);
        role.setName("ROLE_USER");

        return role;
    }
}