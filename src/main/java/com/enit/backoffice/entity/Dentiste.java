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

    @Column(columnDefinition = "TEXT")
    private String diplome;
    
    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Rendezvous> rendezvous;
    
    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceMedical> services;

    public Dentiste() {
        super();
    }

    public String getSpecialiteD() {
        return specialiteD;
    }

    public void setSpecialiteD(String specialiteD) {
        this.specialiteD = specialiteD;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }
    
    public List<Rendezvous> getRendezvous() { return rendezvous; }
    public void setRendezvous(List<Rendezvous> rendezvous) { this.rendezvous = rendezvous; }

    public List<ServiceMedical> getServices() { return services; }
    public void setServices(List<ServiceMedical> services) { this.services = services; }
}