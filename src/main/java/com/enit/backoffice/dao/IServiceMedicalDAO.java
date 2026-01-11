package com.enit.backoffice.dao;

import java.util.List;
import com.enit.backoffice.entity.ServiceMedical;

public interface IServiceMedicalDAO {
    void addService(ServiceMedical service);
    void updateService(ServiceMedical service);
    void deleteService(Integer id);
    ServiceMedical findById(Integer id);
    List<ServiceMedical> findAll();
    List<ServiceMedical> findByDentistId(int dentistId);
}
