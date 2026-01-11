package com.enit.backoffice.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "service_medical")
public class ServiceMedical {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numSM;

    @Column(nullable = false, length = 100)
    private String nomSM;

    @Column(nullable = false, length = 100)
    private String typeSM;

    @Column(columnDefinition = "TEXT")
    private String descriptionSM;

    @Column(precision = 6, scale = 2)
    private BigDecimal tarifSM;

    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToOne
    @JoinColumn(name = "idD", nullable = false)
    private Dentiste dentiste;
    
    @OneToMany(mappedBy = "serviceMedical", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ActeMedical> actes;

    // ===== Getters =====

    public Integer getNumSM() {
        return numSM;
    }

    public String getNomSM() {
        return nomSM;
    }

    public String getTypeSM() {
        return typeSM;
    }

    public String getDescriptionSM() {
        return descriptionSM;
    }

    public BigDecimal getTarifSM() {
        return tarifSM;
    }

    // ===== Setters =====

    public void setNumSM(Integer numSM) {
        this.numSM = numSM;
    }

    public void setNomSM(String nomSM) {
        this.nomSM = nomSM;
    }

    public void setTypeSM(String typeSM) {
        this.typeSM = typeSM;
    }

    public void setDescriptionSM(String descriptionSM) {
        this.descriptionSM = descriptionSM;
    }

    public void setTarifSM(BigDecimal tarifSM) {
        this.tarifSM = tarifSM;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Dentiste getDentiste() { return dentiste; }
    public void setDentiste(Dentiste dentiste) { this.dentiste = dentiste; }
    
    public java.util.List<ActeMedical> getActes() { return actes; }
    public void setActes(java.util.List<ActeMedical> actes) { this.actes = actes; }
}