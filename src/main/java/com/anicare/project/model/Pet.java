package com.anicare.project.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class Pet implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String gender;

    private String id_number;

    private String subtype;

    private String type;

    private String note;

    private String vaccine;

    private String labResults;

    private String imageUrl;

    private boolean hasImage = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointment;

    @ManyToOne
    private Customer customer;

    public Pet() {
        super();
    }

    public Pet(String name, String gender, String idNumber, String subtype, String type, String note, String imageUrl, Date dateOfBirth,
               List<Appointment> appointment, Customer customer) {
        super();
        this.name = name;
        this.gender = gender;
        this.id_number = idNumber;
        this.subtype = subtype;
        this.type = type;
        this.note = note;
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
        this.appointment = appointment;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIdNumber() {
        return id_number;
    }

    public void setIdNumber(String ssn) {
        this.id_number = ssn;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String phone) {
        this.subtype = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String email) {
        this.type = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Appointment> getAppointment() {
        return appointment;
    }

    public void setAppointment(List<Appointment> appointment) {
        this.appointment = appointment;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public String getLabResults() {
        return labResults;
    }

    public void setLabResults(String labResults) {
        this.labResults = labResults;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.hasImage = true;
        this.imageUrl = imageUrl;
    }
}
