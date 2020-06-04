package com.anicare.project.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @NotNull(message = "To date is required")
    private Date toDate;

    private String toTime;

    private String note;

    @NotNull(message = "Pet is required")
    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    @NotNull(message = "Vet is required")
    @ManyToOne
    @JoinColumn(name = "vet_id")
    private Vet vet;

    public Appointment() {
        super();
    }

    public Appointment(Date toDate, String note, Pet pet, Vet vet) {
        super();
        this.toDate = toDate;
        this.note = note;
        this.pet = pet;
        this.vet = vet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getToTime() {
        return toTime;
    }

    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        note = refactor(note);
        this.note = note;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Vet getVet() {
        return vet;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    @Override
    public String toString() {
        return "Appointment [id=" + id + ", toDate=" + toDate + ", note=" + note
                + ", pet=" + pet + ", vet=" + vet + "]";
    }

    private String refactor(String note) {
        String noteNew = note.replaceAll("ğ", "g");
        noteNew = noteNew.replaceAll("ç", "c");
        noteNew = noteNew.replaceAll("ş", "s");
        noteNew = noteNew.replaceAll("ü", "u");
        noteNew = noteNew.replaceAll("ö", "o");
        noteNew = noteNew.replaceAll("ı", "i");
        noteNew = noteNew.replaceAll("Ğ", "G");
        noteNew = noteNew.replaceAll("Ç", "C");
        noteNew = noteNew.replaceAll("Ş", "S");
        noteNew = noteNew.replaceAll("Ü", "U");
        noteNew = noteNew.replaceAll("Ö", "O");
        noteNew = noteNew.replaceAll("İ", "I");

        return noteNew;
    }
}
