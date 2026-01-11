package com.enit.backoffice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "acte_medical")
public class ActeMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idAM;

    @ManyToOne
    @JoinColumn(name = "idRv", nullable = false)
    private Rendezvous rendezvous;

    @ManyToOne
    @JoinColumn(name = "numSM", nullable = false)
    private ServiceMedical serviceMedical;

    @Column(columnDefinition = "TEXT")
    private String descriptionAM;

    @Column(precision = 6, scale = 2)
    private BigDecimal tarifAM;

    public ActeMedical() {}

    public Integer getIdAM() {
        return idAM;
    }

    public void setIdAM(Integer idAM) {
        this.idAM = idAM;
    }

    public Rendezvous getRendezvous() {
        return rendezvous;
    }

    public void setRendezvous(Rendezvous rendezvous) {
        this.rendezvous = rendezvous;
    }

    public ServiceMedical getServiceMedical() {
        return serviceMedical;
    }

    public void setServiceMedical(ServiceMedical serviceMedical) {
        this.serviceMedical = serviceMedical;
    }

    public String getDescriptionAM() {
        return descriptionAM;
    }

    public void setDescriptionAM(String descriptionAM) {
        this.descriptionAM = descriptionAM;
    }

    public BigDecimal getTarifAM() {
        return tarifAM;
    }

    public void setTarifAM(BigDecimal tarifAM) {
        this.tarifAM = tarifAM;
    }
}
