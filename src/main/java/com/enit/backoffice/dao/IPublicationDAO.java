package com.enit.backoffice.dao;

import com.enit.backoffice.entity.Publication;
import jakarta.ejb.Local;
import java.util.List;

@Local
public interface IPublicationDAO {
    void addPublication(Publication p);
    void updatePublication(Publication p);
    void deletePublication(int id);
    Publication findById(int id);
    List<Publication> findAllValid();
    List<Publication> findAllPending();
    List<Publication> findByDentistId(int dentistId);
}
