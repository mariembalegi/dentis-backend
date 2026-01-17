package com.enit.backoffice.service;

import com.enit.backoffice.dao.IActeMedicalDAO;
import com.enit.backoffice.dao.IRendezvousDAO;
import com.enit.backoffice.dao.IServiceMedicalDAO;
import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Singleton
@Startup
public class DataInitializer {

    @EJB private IUserDAO userDAO;
    @EJB private IServiceMedicalDAO serviceMedicalDAO;
    @EJB private IRendezvousDAO rendezvousDAO;
    @EJB private IActeMedicalDAO acteMedicalDAO;

    @PostConstruct
    public void init() {
        System.out.println("DataInitializer: Starting initialization...");
        try {
            if (!userDAO.existsByEmail("admin@dentis.com")) {
                System.out.println("DataInitializer: creating default admin");
                initAdmin();
                initDentistesAndServices();
                initPatientsAndRendezvous();
            }
            if (!userDAO.existsByEmail("meriem.balegi@etudiant-enit.utm.tn")) {
                System.out.println("DataInitializer: creating Mariem admin");
                initMariemAdmin();
            } else {
                 System.out.println("DataInitializer: Mariem admin already exists");
            }
        } catch (Exception e) {
            System.err.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initMariemAdmin() {
        Admin admin = new Admin();
        admin.setNom("Balegi");
        admin.setPrenom("Mariem");
        admin.setEmail("meriem.balegi@etudiant-enit.utm.tn");
        admin.setMotDePasse("root");
        admin.setTel(52072778);
        admin.setSexe(Sexe.F);
        admin.setAdminType("SUPER_ADMIN");
        userDAO.addUser(admin);
    }

    private void initAdmin() {
        Admin admin = new Admin();
        admin.setNom("Super");
        admin.setPrenom("Admin");
        admin.setEmail("admin@dentis.com");
        admin.setMotDePasse("password123");
        admin.setTel(11111111);
        admin.setSexe(Sexe.M);
        admin.setAdminType("SUPER_ADMIN");
        userDAO.addUser(admin);
    }

    private void initDentistesAndServices() {
        // Dentiste 1 (Généraliste & Radio)
        Dentiste d1 = createDentiste("Ben Salah", "Ali", "ali.bensalah@dentis.com", 22222222, Sexe.M, "Doctorat en Medecine Dentaire", "Omnipratique", "Tunis", "Tunis", "Av. Bourguiba");
        createService(d1, "Consultation Simple", "Diagnostic et soins courants", "Examen bucco-dentaire complet", 50.0);
        createService(d1, "Détartrage", "Diagnostic et soins courants", "Détartrage et polissage des deux arcades", 80.0);
        createService(d1, "Radio Panoramique", "Radiologie et imagerie dentaire", "Radiographie de l'ensemble de la mâchoire", 40.0);

        // Dentiste 2 (Endo & Esthétique)
        Dentiste d2 = createDentiste("Mansour", "Mariem", "mariem.mansour@dentis.com", 33333333, Sexe.F, "Diplome d'Esthétique Dentaire", "Esthétique Dentaire", "Sousse", "Sousse Ville", "Rue de la Plage");
        createService(d2, "Traitement Canal", "Endodontie", "Dévitalisation et obturation canalaire", 150.0);
        createService(d2, "Blanchiment", "Esthétique dentaire", "Blanchiment dentaire au fauteuil", 400.0);
        createService(d2, "Facette Céramique", "Esthétique dentaire", "Pose de facette en céramique unitaire", 800.0);

        // Dentiste 3 (Paro & Implant)
        Dentiste d3 = createDentiste("Tounsi", "Ahmed", "ahmed.tounsi@dentis.com", 44444444, Sexe.M, "Diplome Universitaire d'Implantologie", "Implantologie", "Sfax", "Sfax Ouest", "Route de l'Ain");
        createService(d3, "Surfaçage Radiculaire", "Parodontologie", "Nettoyage profond des racines", 200.0);
        createService(d3, "Implant Dentaire", "Implantologie", "Pose d'un implant titane + pilier", 1800.0);
        createService(d3, "Greffe Osseuse", "Implantologie", "Comblement osseux pré-implantaire", 600.0);
    }


    private void initPatientsAndRendezvous() {
        // Patients
        Patient p1 = createPatient("Trabelsi", "Sami", "sami.trabelsi@dentis.com", 55555555, Sexe.M, LocalDate.of(1990, 5, 20), GroupeSanguin.A, "CNAM");
        Patient p2 = createPatient("Jaziri", "Nour", "nour.jaziri@dentis.com", 66666666, Sexe.F, LocalDate.of(1985, 10, 15), GroupeSanguin.O, "Assurance Groupe");
        Patient p3 = createPatient("Kefi", "Ryma", "ryma.kefi@dentis.com", 77777777, Sexe.F, LocalDate.of(1998, 12, 30), GroupeSanguin.B, "Aucun");

        // Retrieve Dentists (assuming they are persisted and we can find them)
        Dentiste d1 = (Dentiste) userDAO.findByEmail("ali.bensalah@dentis.com");
        Dentiste d2 = (Dentiste) userDAO.findByEmail("mariem.mansour@dentis.com");
        
        // Rendezvous for P1 with D1
        Rendezvous rv1 = createRendezvous(p1, d1, LocalDate.now().plusDays(2), LocalTime.of(10, 0), "CONFIRME", "Consultation de suivi");
        
        // We usually link Acte to Rendezvous and Service. 
        // Need to find services of D1. 
        List<ServiceMedical> d1Services = serviceMedicalDAO.findByDentistId(d1.getId());
        if (!d1Services.isEmpty()) {
             createActe(rv1, d1Services.get(0), "Suivi mensuel", 50.0);
        }

        // Rendezvous for P2 with D2
        Rendezvous rv2 = createRendezvous(p2, d2, LocalDate.now().minusDays(1), LocalTime.of(14, 30), "TERMINE", "Douleur dent 46");
        List<ServiceMedical> d2Services = serviceMedicalDAO.findByDentistId(d2.getId());
        if (!d2Services.isEmpty()) {
             createActe(rv2, d2Services.get(1), "Extraction dent 46", 70.0);
        }
    }


    // --- Helper Methods ---

    private Dentiste createDentiste(String nom, String prenom, String email, int tel, Sexe sexe, String diplome, String specialite, String gouvernorat, String delegation, String adresse) {
        Dentiste d = new Dentiste();
        d.setNom(nom);
        d.setPrenom(prenom);
        d.setEmail(email);
        d.setMotDePasse("password123");
        d.setTel(tel);
        d.setSexe(sexe);
        // Removed: d.setSpecialiteD(spec);
        d.setDiplome(diplome);
        d.setSpecialite(specialite);
        d.setGouvernorat(gouvernorat);
        d.setDelegation(delegation);
        d.setAdresse(adresse);
        d.setVerifie(true);
        userDAO.addUser(d);
        // creating a new object from DB to ensure it's managed if needed, 
        // but simplest is just ensuring addUser persists it.
        return (Dentiste) userDAO.findByEmail(email); 
    }

    private Patient createPatient(String nom, String prenom, String email, int tel, Sexe sexe, LocalDate dob, GroupeSanguin blood, String insurance) {
        Patient p = new Patient();
        p.setNom(nom);
        p.setPrenom(prenom);
        p.setEmail(email);
        p.setMotDePasse("password123");
        p.setTel(tel);
        p.setSexe(sexe);
        p.setDateNaissanceP(dob);
        p.setGroupeSanguinP(blood);
        if (insurance != null) {
            try {
                p.setRecouvrementP(com.enit.backoffice.entity.TypeRecouvrement.fromLabel(insurance));
            } catch (Exception e) {}
        }
        userDAO.addUser(p);
        return (Patient) userDAO.findByEmail(email);
    }

    private void createService(Dentiste d, String nom, String type, String desc, double tarif) {
        ServiceMedical s = new ServiceMedical();
        s.setNomSM(nom);
        try {
            s.setTypeSM(com.enit.backoffice.entity.TypeServiceMedical.fromLabel(type));
        } catch (IllegalArgumentException e) {
             // Fallback or ignore in initializer
             System.err.println("Invalid service type: " + type);
        }
        s.setDescriptionSM(desc);
        s.setTarifSM(BigDecimal.valueOf(tarif));
        s.setDentiste(d);
        serviceMedicalDAO.addService(s);
    }

    private Rendezvous createRendezvous(Patient p, Dentiste d, LocalDate date, LocalTime time, String status, String desc) {
        Rendezvous rv = new Rendezvous();
        rv.setPatient(p);
        rv.setDentiste(d);
        rv.setDateRv(date);
        rv.setHeureRv(time);
        rv.setStatutRv(status);
        rv.setDescriptionRv(desc);
        rendezvousDAO.addRendezvous(rv);
        // assuming addRendezvous persists and updates ID or we just return it
        return rv; 
    }

    private void createActe(Rendezvous rv, ServiceMedical sm, String desc, double tarif) {
        ActeMedical a = new ActeMedical();
        a.setRendezvous(rv);
        a.setServiceMedical(sm);
        a.setDescriptionAM(desc);
        a.setTarifAM(BigDecimal.valueOf(tarif));
        acteMedicalDAO.addActe(a);
    }
}
