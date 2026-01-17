package com.enit.backoffice.entity;

import java.util.List;

import jakarta.persistence.*;


@Entity
@Table(
    name = "dentiste"
)
public class Dentiste extends User {
	private static final long serialVersionUID = 1L;

    @Column(columnDefinition = "LONGTEXT")
    private String diplome;
    
    @Column(nullable = true, length = 100)
    private String specialite;

    @Column(nullable = true, length = 100)
    private String gouvernorat;

    @Column(nullable = true, length = 100)
    private String delegation;

    @Column(nullable = true, length = 255)
    private String adresse;

    private boolean verifie;

    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Publication> publications;

    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Rendezvous> rendezvous;
    


    @OneToMany(mappedBy = "dentiste", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horaire> horaires;

    public Dentiste() {
        super();
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }
    
    public String getGouvernorat() {
        return gouvernorat;
    }

    public void setGouvernorat(String gouvernorat) {
        this.gouvernorat = gouvernorat;
    }

    public String getDelegation() {
        return delegation;
    }

    public void setDelegation(String delegation) {
        this.delegation = delegation;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public boolean isVerifie() {
        return verifie;
    }

    public void setVerifie(boolean verifie) {
        this.verifie = verifie;
    }

    public List<Publication> getPublications() {
        return publications;
    }

    public void setPublications(List<Publication> publications) {
        this.publications = publications;
    }
    
    public List<Rendezvous> getRendezvous() { return rendezvous; }
    public void setRendezvous(List<Rendezvous> rendezvous) { this.rendezvous = rendezvous; }

    public List<ServiceMedical> getServices() { return services; }
    
    public List<Horaire> getHoraires() {
        return horaires;
    }

    public void setHoraires(List<Horaire> horaires) {
        this.horaires = horaires;
    }
    public void setServices(List<ServiceMedical> services) { this.services = services; }
}