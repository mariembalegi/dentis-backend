package com.enit.backoffice.dao;

import java.util.List;
import com.enit.backoffice.entity.Rendezvous;

public interface IRendezvousDAO {
    void addRendezvous(Rendezvous rv);
    void updateRendezvous(Rendezvous rv);
    void deleteRendezvous(Integer id);
    Rendezvous findById(Integer id);
    List<Rendezvous> findByDentistId(int dentistId);
    List<Rendezvous> findByPatientId(int patientId);
    Rendezvous findAvailableSlot(int dentistId, java.time.LocalDate date, java.time.LocalTime time);
}
