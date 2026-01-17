package com.enit.backoffice.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "patient")
public class Patient extends User {

    private static final long serialVersionUID = 1L;

    @Column(name = "dateNaissanceP")
    private LocalDate dateNaissanceP;

    @Column(name = "groupeSanguinP", length = 5)
    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguinP; 

    @Column(name = "recouvrementP", length = 100)
    @Enumerated(EnumType.STRING)
    private TypeRecouvrement recouvrementP;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Rendezvous> rendezvous;

    public Patient() {
        super();
    }

    public LocalDate getDateNaissanceP() {
        return dateNaissanceP;
    }
    public void setDateNaissanceP(LocalDate dateNaissanceP) {
        this.dateNaissanceP = dateNaissanceP;
    }

    public GroupeSanguin getGroupeSanguinP() {
        return groupeSanguinP;
    }
    public void setGroupeSanguinP(GroupeSanguin groupeSanguinP) {
        this.groupeSanguinP = groupeSanguinP;
    }

    public TypeRecouvrement getRecouvrementP() {
        return recouvrementP;
    }
    public void setRecouvrementP(TypeRecouvrement recouvrementP) {
        this.recouvrementP = recouvrementP;
    }
    
    public List<Rendezvous> getRendezvous() { return rendezvous; }
    public void setRendezvous(List<Rendezvous> rendezvous) { this.rendezvous = rendezvous; }
}
