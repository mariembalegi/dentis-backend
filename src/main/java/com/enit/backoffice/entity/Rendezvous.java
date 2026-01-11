package com.enit.backoffice.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "rendez_vous")
public class Rendezvous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRv;

    @ManyToOne
    @JoinColumn(name = "idP", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "idD", nullable = false)
    private Dentiste dentiste; // correction du nom de variable

    @OneToMany(mappedBy = "rendezvous", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ActeMedical> actes;

    private LocalDate dateRv;
    private LocalTime heureRv;

    @Column(nullable = false, length = 100)
    private String statutRv;

    @Column(columnDefinition = "TEXT")
    private String descriptionRv;

    // ----------------------
    // Getters et Setters
    // ----------------------

    public Integer getIdRv() {
        return idRv;
    }

    public void setIdRv(Integer idRv) {
        this.idRv = idRv;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Dentiste getDentiste() {
        return dentiste;
    }

    public void setDentiste(Dentiste dentiste) {
        this.dentiste = dentiste;
    }

    public java.util.List<ActeMedical> getActes() {
        return actes;
    }

    public void setActes(java.util.List<ActeMedical> actes) {
        this.actes = actes;
    }

    public LocalDate getDateRv() {
        return dateRv;
    }

    public void setDateRv(LocalDate dateRv) {
        this.dateRv = dateRv;
    }

    public LocalTime getHeureRv() {
        return heureRv;
    }

    public void setHeureRv(LocalTime heureRv) {
        this.heureRv = heureRv;
    }

    public String getStatutRv() {
        return statutRv;
    }

    public void setStatutRv(String statutRv) {
        this.statutRv = statutRv;
    }

    public String getDescriptionRv() {
        return descriptionRv;
    }

    public void setDescriptionRv(String descriptionRv) {
        this.descriptionRv = descriptionRv;
    }
}
