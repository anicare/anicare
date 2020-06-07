package com.anicare.project.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Shelter implements Comparable<Shelter> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String photoUrl;

    private String name;

    private String location;

    private String address;

    private String pets;

    private String phoneNumber;

    private String webAddress;

    public Shelter() {
        super();
    }

    public Shelter(String photoUrl, String name, String location, String address, String pets, String phoneNumber, String webAddress) {
        super();
        this.photoUrl = photoUrl;
        this.name = name;
        this.location = location;
        this.address = address;
        this.pets = pets;
        this.phoneNumber = phoneNumber;
        this.webAddress = webAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPets() {
        return pets;
    }

    public void setPets(String pets) {
        this.pets = pets;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    @Override
    public int compareTo(Shelter o) {
        return this.getLocation().compareTo(o.getLocation());
    }
}
