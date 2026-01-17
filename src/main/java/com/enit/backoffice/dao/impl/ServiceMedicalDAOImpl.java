package com.enit.backoffice.dao.impl;

import java.util.List;
import com.enit.backoffice.dao.IServiceMedicalDAO;
import com.enit.backoffice.entity.ServiceMedical;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ServiceMedicalDAOImpl implements IServiceMedicalDAO {

    @PersistenceContext(unitName = "dentisUnit")
    EntityManager em;

    @Override
    public void addService(ServiceMedical service) {
        em.persist(service);
    }

    @Override
    public void updateService(ServiceMedical service) {
        em.merge(service);
    }

    @Override
    public void deleteService(Integer id) {
        ServiceMedical sm = em.find(ServiceMedical.class, id);
        if(sm != null) em.remove(sm);
    }

    @Override
    public ServiceMedical findById(Integer id) {
        return em.find(ServiceMedical.class, id);
    }

    @Override
    public List<ServiceMedical> findAll() {
        return em.createQuery("SELECT s FROM ServiceMedical s", ServiceMedical.class).getResultList();
    }

    @Override
    public List<ServiceMedical> findByDentistId(int dentistId) {
        return em.createQuery("SELECT s FROM ServiceMedical s WHERE s.dentiste.id = :dentistId", ServiceMedical.class)
                 .setParameter("dentistId", dentistId)
                 .getResultList();
    }

    @Override
    public List<String> findServiceNamesLike(String keyword) {
        return em.createQuery("SELECT DISTINCT s.nomSM FROM ServiceMedical s WHERE LOWER(s.nomSM) LIKE :keyword", String.class)
                 .setParameter("keyword", "%" + keyword.toLowerCase() + "%")
                 .setMaxResults(10)
                 .getResultList();
    }
}
