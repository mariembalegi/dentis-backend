package com.enit.backoffice.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "service_medical")
public class ServiceMedical implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numSM;

    @Column(nullable = false, length = 100)
    private String nomSM;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private TypeServiceMedical typeSM;

    @Column(columnDefinition = "TEXT")
    private String descriptionSM;

    @Column(precision = 6, scale = 2)
    private BigDecimal tarifSM;

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

    public TypeServiceMedical getTypeSM() {
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

    public void setTypeSM(TypeServiceMedical typeSM) {
        this.typeSM = typeSM;
    }

    public void setDescriptionSM(String descriptionSM) {
        this.descriptionSM = descriptionSM;
    }

    public void setTarifSM(BigDecimal tarifSM) {
        this.tarifSM = tarifSM;
    }

    public Dentiste getDentiste() { return dentiste; }
    public void setDentiste(Dentiste dentiste) { this.dentiste = dentiste; }
    
    public java.util.List<ActeMedical> getActes() { return actes; }
    public void setActes(java.util.List<ActeMedical> actes) { this.actes = actes; }
}