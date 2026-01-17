package com.enit.backoffice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "publication")
public class Publication implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPub;

    @Column(nullable = false)
    private String titrePub;

    @Column
    @Temporal(TemporalType.DATE)
    private Date datePub;

    @Enumerated(EnumType.STRING)
    @Column
    private TypePublication typePub;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String fichierPub;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String affichePub;

    @Column
    private boolean valide;

    @ManyToOne
    @JoinColumn(name = "idD", nullable = false)
    private Dentiste dentiste;

    public Publication() {
    }

    public Integer getIdPub() {
        return idPub;
    }

    public void setIdPub(Integer idPub) {
        this.idPub = idPub;
    }

    public String getTitrePub() {
        return titrePub;
    }

    public void setTitrePub(String titrePub) {
        this.titrePub = titrePub;
    }

    public Date getDatePub() {
        return datePub;
    }

    public void setDatePub(Date datePub) {
        this.datePub = datePub;
    }

    public TypePublication getTypePub() {
        return typePub;
    }

    public void setTypePub(TypePublication typePub) {
        this.typePub = typePub;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFichierPub() {
        return fichierPub;
    }

    public void setFichierPub(String fichierPub) {
        this.fichierPub = fichierPub;
    }

    public String getAffichePub() {
        return affichePub;
    }

    public void setAffichePub(String affichePub) {
        this.affichePub = affichePub;
    }

    public boolean isValide() {
        return valide;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public Dentiste getDentiste() {
        return dentiste;
    }

    public void setDentiste(Dentiste dentiste) {
        this.dentiste = dentiste;
    }
}
