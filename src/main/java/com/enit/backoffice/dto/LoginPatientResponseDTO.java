package com.enit.backoffice.dto;
import java.time.LocalDate;

public class LoginPatientResponseDTO extends LoginUserResponseDTO {
    private LocalDate dateNaissanceP;
    private String groupeSanguinP;
    private String recouvrementP;

    // Getters et Setters
    public LocalDate getDateNaissanceP() { return dateNaissanceP; }
    public void setDateNaissanceP(LocalDate dateNaissanceP) { this.dateNaissanceP = dateNaissanceP; }

    public String getGroupeSanguinP() { return groupeSanguinP; }
    public void setGroupeSanguinP(String groupeSanguinP) { this.groupeSanguinP = groupeSanguinP; }

    public String getRecouvrementP() { return recouvrementP; }
    public void setRecouvrementP(String recouvrementP) { this.recouvrementP = recouvrementP; }
}