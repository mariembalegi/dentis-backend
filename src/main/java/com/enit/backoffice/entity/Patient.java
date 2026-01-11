package com.enit.backoffice.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "patient")
public class Patient extends User {

    private static final long serialVersionUID = 1L;

    @Column(name = "dateNaissanceP")
    private LocalDate dateNaissanceP;

    @Column(name = "groupeSanguinP", length = 2)
    private String groupeSanguinP; // A, B, O, AB

    @Column(name = "recouvrementP", length = 100)
    private String recouvrementP;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Rendezvous> rendezvous;

    // 🔹 Constructeur par défaut 
    public Patient() {
        super();
    }

    // 🔹 Getters & Setters
    public LocalDate getDateNaissanceP() {
        return dateNaissanceP;
    }
    public void setDateNaissanceP(LocalDate dateNaissanceP) {
        this.dateNaissanceP = dateNaissanceP;
    }

    public String getGroupeSanguinP() {
        return groupeSanguinP;
    }
    public void setGroupeSanguinP(String groupeSanguinP) {
        this.groupeSanguinP = groupeSanguinP;
    }

    public String getRecouvrementP() {
        return recouvrementP;
    }
    public void setRecouvrementP(String recouvrementP) {
        this.recouvrementP = recouvrementP;
    }
    
    public List<Rendezvous> getRendezvous() { return rendezvous; }
    public void setRendezvous(List<Rendezvous> rendezvous) { this.rendezvous = rendezvous; }
}
