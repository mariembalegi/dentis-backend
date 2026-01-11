package com.enit.backoffice.dto;

import java.time.LocalDate;

public class SignupPatientRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int tel;
    private String sexe;
    private String photo;
    
    private LocalDate dateNaissanceP;
    private String groupeSanguinP;
    private String recouvrementP;

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }
    public int getTel() { return tel; }
    public void setTel(int tel) { this.tel = tel; }
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    public LocalDate getDateNaissanceP() { return dateNaissanceP; }
    public void setDateNaissanceP(LocalDate dateNaissanceP) { this.dateNaissanceP = dateNaissanceP; }
    public String getGroupeSanguinP() { return groupeSanguinP; }
    public void setGroupeSanguinP(String groupeSanguinP) { this.groupeSanguinP = groupeSanguinP; }
    public String getRecouvrementP() { return recouvrementP; }
    public void setRecouvrementP(String recouvrementP) { this.recouvrementP = recouvrementP; }
}
