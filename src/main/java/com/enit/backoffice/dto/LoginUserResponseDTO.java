package com.enit.backoffice.dto;

public abstract class LoginUserResponseDTO {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String photo;
    private String sexe;
    private int tel;
    private String token;


    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }
    
    public String getSexe() { return sexe; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    
    public int getTel() { return tel; }
    public void setTel(int tel) { this.tel = tel; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
