package com.enit.backoffice.dao.impl;

import java.util.List;

import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.entity.Patient;
import com.enit.backoffice.entity.User;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class UserDAOImpl implements IUserDAO {

	@PersistenceContext(unitName = "dentisUnit")
	EntityManager em;
	
	

	@Override
	public User findByEmail(String email) {
	    List<User> users = em.createQuery(
	        "SELECT u FROM User u WHERE u.email = :email", User.class)
	        .setParameter("email", email)
	        .setMaxResults(1)
	        .getResultList();

	    return users.isEmpty() ? null : users.get(0);
	}

  

    @Override
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

	@Override
	public java.util.List<Patient> getAllPatients() {
		return em.createQuery("SELECT p FROM Patient p", Patient.class).getResultList();
	}

    
    @Override
    public void addUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        user.setMotDePasse(org.mindrot.jbcrypt.BCrypt.hashpw(user.getMotDePasse(), org.mindrot.jbcrypt.BCrypt.gensalt()));
        em.persist(user);
    }

    @Override
    public User findById(int id) {
        return em.find(User.class, id);
    }

    @Override
    public java.util.List<com.enit.backoffice.entity.Dentiste> searchDentists(String keyword, String location) {
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT d FROM Dentiste d LEFT JOIN d.services s WHERE 1=1 ");
        
        boolean standardSearch = true;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim().toLowerCase();
            if (k.contains("dentisterie générale") || k.contains("dentisterie generale")) {
                queryStr.append(" AND s.typeSM IN (com.enit.backoffice.entity.TypeServiceMedical.DIAGNOSTIC_SOINS_COURANTS, com.enit.backoffice.entity.TypeServiceMedical.PARODONTOLOGIE, com.enit.backoffice.entity.TypeServiceMedical.RADIOLOGIE_IMAGERIE)");
                standardSearch = false;
            } else if (k.contains("actes chirurgicaux")) {
                queryStr.append(" AND s.typeSM IN (com.enit.backoffice.entity.TypeServiceMedical.ENDODONTIE, com.enit.backoffice.entity.TypeServiceMedical.ESTHETIQUE_DENTAIRE, com.enit.backoffice.entity.TypeServiceMedical.IMPLANTOLOGIE)");
                standardSearch = false;
            } else {
                queryStr.append(" AND (LOWER(d.nom) LIKE :keyword OR LOWER(d.prenom) LIKE :keyword OR LOWER(s.nomSM) LIKE :keyword OR LOWER(s.typeSM) LIKE :keyword)");
            }
        }
        
        if (location != null && !location.trim().isEmpty()) {
            queryStr.append(" AND (LOWER(d.gouvernorat) LIKE :location OR LOWER(d.delegation) LIKE :location OR LOWER(d.adresse) LIKE :location)");
        }
        
        jakarta.persistence.TypedQuery<com.enit.backoffice.entity.Dentiste> query = em.createQuery(queryStr.toString(), com.enit.backoffice.entity.Dentiste.class);
        
        if (standardSearch && keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword.toLowerCase() + "%");
        }
        
        if (location != null && !location.trim().isEmpty()) {
            query.setParameter("location", "%" + location.toLowerCase() + "%");
        }
        
        return query.getResultList();
    }

    @Override
    public java.util.List<com.enit.backoffice.entity.Dentiste> findDentistsByName(String keyword) {
        return em.createQuery("SELECT d FROM Dentiste d WHERE LOWER(d.nom) LIKE :keyword OR LOWER(d.prenom) LIKE :keyword", com.enit.backoffice.entity.Dentiste.class)
                 .setParameter("keyword", "%" + keyword.toLowerCase() + "%")
                 .setMaxResults(5)
                 .getResultList();
    }

    @Override
    public java.util.List<com.enit.backoffice.entity.ServiceMedical> getDentistServices(int dentistId) {
        return em.createQuery("SELECT s FROM ServiceMedical s WHERE s.dentiste.id = :id", com.enit.backoffice.entity.ServiceMedical.class)
                 .setParameter("id", dentistId)
                 .getResultList();
    }

    @Override
    public java.util.List<com.enit.backoffice.entity.Rendezvous> getAvailableRendezvous(int dentistId) {
        return em.createQuery("SELECT r FROM Rendezvous r WHERE r.dentiste.id = :id AND r.statutRv = :status", com.enit.backoffice.entity.Rendezvous.class)
                 .setParameter("id", dentistId)
                 .setParameter("status", com.enit.backoffice.entity.StatutRendezvous.DISPONIBLE)
                 .getResultList();
    }

    @Override
    public void updateDentist(com.enit.backoffice.entity.Dentiste dentiste) {
        em.merge(dentiste);
    }

    @Override
    public java.util.List<com.enit.backoffice.entity.Horaire> getDentistHoraires(int dentistId) {
        return em.createQuery("SELECT h FROM Horaire h WHERE h.dentiste.id = :id ORDER BY h.jourSemaine", com.enit.backoffice.entity.Horaire.class)
                 .setParameter("id", dentistId)
                 .getResultList();
    }

    @Override
    public void updateDentistHoraires(int dentistId, java.util.List<com.enit.backoffice.entity.Horaire> horaires) {
        // First delete existing schedules to avoid duplicates/conflicts or complex merge logic
        em.createQuery("DELETE FROM Horaire h WHERE h.dentiste.id = :id")
          .setParameter("id", dentistId)
          .executeUpdate();
          
        // Re-insert new schedules
        com.enit.backoffice.entity.Dentiste d = em.find(com.enit.backoffice.entity.Dentiste.class, dentistId);
        if (d != null) {
            for (com.enit.backoffice.entity.Horaire h : horaires) {
                h.setDentiste(d); // Ensure relationship is set
                em.persist(h);
            }
        }
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    @Override
    public void updateUser(User user) {
        em.merge(user);
    }

    @Override
    public void deleteUser(int id) {
        User user = em.find(User.class, id);
        if (user != null) {
            em.remove(user);
        }
    }

    @Override
    public long countPatients() {
        return em.createQuery("SELECT COUNT(p) FROM Patient p", Long.class).getSingleResult();
    }

    @Override
    public long countDentistes() {
        return em.createQuery("SELECT COUNT(d) FROM Dentiste d", Long.class).getSingleResult();
    }

    @Override
    public long countDistinctGouvernorats() {
        return em.createQuery("SELECT COUNT(DISTINCT d.gouvernorat) FROM Dentiste d WHERE d.gouvernorat IS NOT NULL AND d.gouvernorat <> ''", Long.class).getSingleResult();
    }
}
