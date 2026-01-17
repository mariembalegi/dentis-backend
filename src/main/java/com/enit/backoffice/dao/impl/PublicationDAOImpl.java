package com.enit.backoffice.dao.impl;

import com.enit.backoffice.dao.IPublicationDAO;
import com.enit.backoffice.entity.Publication;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PublicationDAOImpl implements IPublicationDAO {

    @PersistenceContext(unitName = "dentisUnit")
    EntityManager em;

    @Override
    public void addPublication(Publication p) {
        em.persist(p);
    }

    @Override
    public void updatePublication(Publication p) {
        em.merge(p);
    }

    @Override
    public void deletePublication(int id) {
        Publication p = em.find(Publication.class, id);
        if (p != null) {
            em.remove(p);
        }
    }

    @Override
    public Publication findById(int id) {
        return em.find(Publication.class, id);
    }

    @Override
    public List<Publication> findAllValid() {
        return em.createQuery("SELECT p FROM Publication p WHERE p.valide = true ORDER BY p.datePub DESC", Publication.class)
                .getResultList();
    }

    @Override
    public List<Publication> findAllPending() {
        return em.createQuery("SELECT p FROM Publication p WHERE p.valide = false ORDER BY p.datePub ASC", Publication.class)
                .getResultList();
    }

    @Override
    public List<Publication> findByDentistId(int dentistId) {
        return em.createQuery("SELECT p FROM Publication p WHERE p.dentiste.id = :dentistId ORDER BY p.datePub DESC", Publication.class)
                .setParameter("dentistId", dentistId)
                .getResultList();
    }
}
