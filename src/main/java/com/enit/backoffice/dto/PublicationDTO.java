package com.enit.backoffice.dto;

import java.util.Date;

public class PublicationDTO {
    private Integer idPub;
    private String titrePub;
    private Date datePub;
    private String typePub;
    private String description;
    private String fichierPub;
    private String affichePub;
    private boolean valide;
    private int dentistId;
    private String dentistName;

    public PublicationDTO() {}

    public Integer getIdPub() { return idPub; }
    public void setIdPub(Integer idPub) { this.idPub = idPub; }

    public String getTitrePub() { return titrePub; }
    public void setTitrePub(String titrePub) { this.titrePub = titrePub; }

    public Date getDatePub() { return datePub; }
    public void setDatePub(Date datePub) { this.datePub = datePub; }

    public String getTypePub() { return typePub; }
    public void setTypePub(String typePub) { this.typePub = typePub; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFichierPub() { return fichierPub; }
    public void setFichierPub(String fichierPub) { this.fichierPub = fichierPub; }

    public String getAffichePub() { return affichePub; }
    public void setAffichePub(String affichePub) { this.affichePub = affichePub; }

    public boolean isValide() { return valide; }
    public void setValide(boolean valide) { this.valide = valide; }

    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }

    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }
}
