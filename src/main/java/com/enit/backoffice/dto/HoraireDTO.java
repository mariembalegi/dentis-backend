package com.enit.backoffice.dto;

import java.io.Serializable;

public class HoraireDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String jourSemaine;
    
    private String matinDebut;

    private String matinFin;
    
    private String apresMidiDebut;
    
    private String apresMidiFin;
    
    private boolean estFerme;

    public String getJourSemaine() { return jourSemaine; }
    public void setJourSemaine(String jourSemaine) { this.jourSemaine = jourSemaine; }
    
    public String getMatinDebut() { return matinDebut; }
    public void setMatinDebut(String matinDebut) { this.matinDebut = matinDebut; }
    
    public String getMatinFin() { return matinFin; }
    public void setMatinFin(String matinFin) { this.matinFin = matinFin; }
    
    public String getApresMidiDebut() { return apresMidiDebut; }
    public void setApresMidiDebut(String apresMidiDebut) { this.apresMidiDebut = apresMidiDebut; }
    
    public String getApresMidiFin() { return apresMidiFin; }
    public void setApresMidiFin(String apresMidiFin) { this.apresMidiFin = apresMidiFin; }
    
    public boolean isEstFerme() { return estFerme; }
    public void setEstFerme(boolean estFerme) { this.estFerme = estFerme; }
}
