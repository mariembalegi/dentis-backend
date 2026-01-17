package com.enit.backoffice.dto;

import java.io.Serializable;
import java.time.LocalTime;

public class HoraireDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String jourSemaine;
    private LocalTime matinDebut;
    private LocalTime matinFin;
    private LocalTime apresMidiDebut;
    private LocalTime apresMidiFin;
    private boolean estFerme;

    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }
    
    public LocalTime getMatinDebut() { return matinDebut; }
    public void setMatinDebut(LocalTime matinDebut) { this.matinDebut = matinDebut; }
    
    public LocalTime getMatinFin() { return matinFin; }
    public void setMatinFin(LocalTime matinFin) { this.matinFin = matinFin; }
    
    public LocalTime getApresMidiDebut() { return apresMidiDebut; }
    public void setApresMidiDebut(LocalTime apresMidiDebut) { this.apresMidiDebut = apresMidiDebut; }
    
    public LocalTime getApresMidiFin() { return apresMidiFin; }
    public void setApresMidiFin(LocalTime apresMidiFin) { this.apresMidiFin = apresMidiFin; }
    
    public boolean isEstFerme() { return estFerme; }
    public void setEstFerme(boolean estFerme) { this.estFerme = estFerme; }
}
