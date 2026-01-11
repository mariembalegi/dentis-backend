package com.enit.backoffice.dao;

import com.enit.backoffice.entity.ActeMedical;
import java.util.List;

public interface IActeMedicalDAO {
    void addActe(ActeMedical acte);
    void updateActe(ActeMedical acte);
    void deleteActe(Integer id);
    ActeMedical findById(Integer id);
    List<ActeMedical> findByRendezvousId(Integer idRv);
}
