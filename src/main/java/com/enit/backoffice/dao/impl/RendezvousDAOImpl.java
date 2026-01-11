package com.enit.backoffice.dao.impl;

import java.util.List;
import com.enit.backoffice.dao.IRendezvousDAO;
import com.enit.backoffice.entity.Rendezvous;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class RendezvousDAOImpl implements IRendezvousDAO {

    @PersistenceContext(unitName = "dentisUnit")
    EntityManager em;

    @Override
    public void addRendezvous(Rendezvous rv) {
        em.persist(rv);
    }

    @Override
    public void updateRendezvous(Rendezvous rv) {
        em.merge(rv);
    }

    @Override
    public void deleteRendezvous(Integer id) {
        Rendezvous rv = em.find(Rendezvous.class, id);
        if(rv != null) em.remove(rv);
    }

    @Override
    public Rendezvous findById(Integer id) {
        return em.find(Rendezvous.class, id);
    }

    @Override
    public List<Rendezvous> findByDentistId(int dentistId) {
        return em.createQuery("SELECT r FROM Rendezvous r WHERE r.dentiste.id = :id", Rendezvous.class)
                 .setParameter("id", dentistId)
                 .getResultList();
    }

    @Override
    public List<Rendezvous> findByPatientId(int patientId) {
        return em.createQuery("SELECT r FROM Rendezvous r WHERE r.patient.id = :id", Rendezvous.class)
                 .setParameter("id", patientId)
                 .getResultList();
    }
}
