package com.enit.backoffice.dto;
public class LoginDentisteResponseDTO extends LoginUserResponseDTO {
    private String specialiteD;

    public String getSpecialiteD() { return specialiteD; }
    public void setSpecialiteD(String specialiteD) { this.specialiteD = specialiteD; }
}