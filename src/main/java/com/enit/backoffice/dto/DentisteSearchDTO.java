package com.enit.backoffice.dto;

public class DentisteSearchDTO {
    private int id;
    private String nom;
    private String prenom;
    private String gouvernorat;
    private String delegation;
    private String adresse;
    private String photo; // Optional, for the avatar
    private String diplome;
    private String specialite;

    public DentisteSearchDTO(int id, String nom, String prenom, String gouvernorat, String delegation, String adresse, String diplome, String specialite) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.gouvernorat = gouvernorat;
        this.delegation = delegation;
        this.adresse = adresse;
        this.diplome = diplome;
        this.specialite = specialite;
    }
    
    public DentisteSearchDTO() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getGouvernorat() { return gouvernorat; }
    public void setGouvernorat(String gouvernorat) { this.gouvernorat = gouvernorat; }
    
    public String getDelegation() { return delegation; }
    public void setDelegation(String delegation) { this.delegation = delegation; }
    
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
}
