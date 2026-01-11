package com.enit.backoffice.dao.impl;

import com.enit.backoffice.dao.IActeMedicalDAO;
import com.enit.backoffice.entity.ActeMedical;
import java.util.List;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ActeMedicalDAOImpl implements IActeMedicalDAO {

    @PersistenceContext(unitName = "dentisUnit")
    EntityManager em;

    @Override
    public void addActe(ActeMedical acte) {
        em.persist(acte);
    }

    @Override
    public void updateActe(ActeMedical acte) {
        em.merge(acte);
    }

    @Override
    public void deleteActe(Integer id) {
        ActeMedical a = em.find(ActeMedical.class, id);
        if(a != null) em.remove(a);
    }

    @Override
    public ActeMedical findById(Integer id) {
        return em.find(ActeMedical.class, id);
    }

    @Override
    public List<ActeMedical> findByRendezvousId(Integer idRv) {
        return em.createQuery("SELECT a FROM ActeMedical a WHERE a.rendezvous.idRv = :idRv", ActeMedical.class)
                 .setParameter("idRv", idRv)
                 .getResultList();
    }
}
