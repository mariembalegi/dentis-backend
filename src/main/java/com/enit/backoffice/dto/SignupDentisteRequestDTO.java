package com.enit.backoffice.dto;

public class SignupDentisteRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private int tel;
    private String sexe;
    private String photo;
    
    private String diplome;
    private String ville;

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
    public String getDiplome() { return diplome; }
    public void setDiplome(String diplome) { this.diplome = diplome; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; } 
}
