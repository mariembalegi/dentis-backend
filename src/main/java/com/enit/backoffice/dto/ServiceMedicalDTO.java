package com.enit.backoffice.dto;
import java.math.BigDecimal;

public class ServiceMedicalDTO {
    private Integer numSM;
    private String nomSM;
    private String typeSM;
    private String descriptionSM;
    private BigDecimal tarifSM;
    private int dentistId;

    public Integer getNumSM() { return numSM; }
    public void setNumSM(Integer numSM) { this.numSM = numSM; }
    public String getNomSM() { return nomSM; }
    public void setNomSM(String nomSM) { this.nomSM = nomSM; }
    public String getTypeSM() { return typeSM; }
    public void setTypeSM(String typeSM) { this.typeSM = typeSM; }
    public String getDescriptionSM() { return descriptionSM; }
    public void setDescriptionSM(String descriptionSM) { this.descriptionSM = descriptionSM; }
    public BigDecimal getTarifSM() { return tarifSM; }
    public void setTarifSM(BigDecimal tarifSM) { this.tarifSM = tarifSM; }
    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }
}
