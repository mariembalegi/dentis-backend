package com.enit.backoffice.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalTime;

@Entity
@Table(name = "horaire")
public class Horaire implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer idHoraire;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JourSemaine jourSemaine;

    @Column
    private LocalTime matinDebut;

    @Column
    private LocalTime matinFin;

    @Column
    private LocalTime apresMidiDebut;

    @Column
    private LocalTime apresMidiFin;

    @Column(nullable = false)
    private boolean estFerme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idD", nullable = false)
    private Dentiste dentiste;

    public Horaire() {}

    public Integer getIdHoraire() {
        return idHoraire;
    }

    public void setIdHoraire(Integer idHoraire) {
        this.idHoraire = idHoraire;
    }

    public JourSemaine getJourSemaine() {
        return jourSemaine;
    }

    public void setJourSemaine(JourSemaine jourSemaine) {
        this.jourSemaine = jourSemaine;
    }

    public LocalTime getMatinDebut() {
        return matinDebut;
    }

    public void setMatinDebut(LocalTime matinDebut) {
        this.matinDebut = matinDebut;
    }

    public LocalTime getMatinFin() {
        return matinFin;
    }

    public void setMatinFin(LocalTime matinFin) {
        this.matinFin = matinFin;
    }

    public LocalTime getApresMidiDebut() {
        return apresMidiDebut;
    }

    public void setApresMidiDebut(LocalTime apresMidiDebut) {
        this.apresMidiDebut = apresMidiDebut;
    }

    public LocalTime getApresMidiFin() {
        return apresMidiFin;
    }

    public void setApresMidiFin(LocalTime apresMidiFin) {
        this.apresMidiFin = apresMidiFin;
    }

    public boolean isEstFerme() {
        return estFerme;
    }

    public void setEstFerme(boolean estFerme) {
        this.estFerme = estFerme;
    }

    public Dentiste getDentiste() {
        return dentiste;
    }

    public void setDentiste(Dentiste dentiste) {
        this.dentiste = dentiste;
    }
}
