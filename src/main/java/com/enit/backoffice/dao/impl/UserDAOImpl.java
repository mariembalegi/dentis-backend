package com.enit.backoffice.dao.impl;

import java.util.List;

import com.enit.backoffice.dao.IUserDAO;
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
    public void addUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé !");
        }
        user.setMotDePasse(org.mindrot.jbcrypt.BCrypt.hashpw(user.getMotDePasse(), org.mindrot.jbcrypt.BCrypt.gensalt()));
        em.persist(user);
    }
}
