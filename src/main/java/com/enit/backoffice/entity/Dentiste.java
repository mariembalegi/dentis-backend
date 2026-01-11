package com.enit.backoffice.entity;

import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(
    name = "dentiste"
)
public class Dentiste extends User {
	private static final long serialVersionUID = 1L;

    @Column(name = "specialiteD", length = 100)
    private String specialiteD;
    
    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Rendezvous> rendezvous;

    // Constructeur par défaut 
    public Dentiste() {
        super();
    }

    // Getters et Setters
    public String getSpecialiteD() {
        return specialiteD;
    }

    public void setSpecialiteD(String specialiteD) {
        this.specialiteD = specialiteD;
    }
    
    public List<Rendezvous> getRendezvous() { return rendezvous; }
    public void setRendezvous(List<Rendezvous> rendezvous) { this.rendezvous = rendezvous; }

}