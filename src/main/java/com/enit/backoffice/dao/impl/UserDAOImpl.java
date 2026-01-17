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
                queryStr.append(" AND s.typeSM IN ('Diagnostic et soins courants', 'Parodontologie', 'Radiologie et imagerie dentaire')");
                standardSearch = false;
            } else if (k.contains("actes chirurgicaux")) {
                queryStr.append(" AND s.typeSM IN ('Endodontie', 'Esthétique dentaire', 'Implantologie')");
                standardSearch = false;
            } else {
                queryStr.append(" AND (LOWER(d.nom) LIKE :keyword OR LOWER(d.prenom) LIKE :keyword OR LOWER(s.typeSM) LIKE :keyword)");
            }
        }
        
        if (location != null && !location.trim().isEmpty()) {
            queryStr.append(" AND LOWER(d.ville) LIKE :location");
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
}
