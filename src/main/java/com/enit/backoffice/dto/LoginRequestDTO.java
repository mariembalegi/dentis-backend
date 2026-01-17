package com.enit.backoffice.dto;

public class LoginRequestDTO {

    private String email;

    private String motDePasse;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    // Allow "password" from frontend to map to "motDePasse"
    public void setPassword(String password) {
        this.motDePasse = password;
    }
}