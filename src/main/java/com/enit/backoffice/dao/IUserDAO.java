package com.enit.backoffice.dao;



import com.enit.backoffice.entity.Patient;
import com.enit.backoffice.entity.User;
import java.util.List;

import jakarta.ejb.Local;

@Local
public interface IUserDAO {
	
	public List<Patient> getAllPatients();
	
	 // Récupérer utilisateur par email 
    public User findByEmail(String email);

    
 // Vérifier l’existence d’un email 
    public boolean existsByEmail(String email);

    
    // Ajouter User
    public void addUser(User user);
    
    public User findById(int id);
    
    // Rechercher les dentistes
    public java.util.List<com.enit.backoffice.entity.Dentiste> searchDentists(String keyword, String location);

    public java.util.List<com.enit.backoffice.entity.Dentiste> findDentistsByName(String keyword);
    
    public java.util.List<com.enit.backoffice.entity.ServiceMedical> getDentistServices(int dentistId);
    
    public java.util.List<com.enit.backoffice.entity.Rendezvous> getAvailableRendezvous(int dentistId);
    
    // Manage Dentist Profile & Schedule
    public void updateDentist(com.enit.backoffice.entity.Dentiste dentiste);
    public java.util.List<com.enit.backoffice.entity.Horaire> getDentistHoraires(int dentistId);
    public void updateDentistHoraires(int dentistId, java.util.List<com.enit.backoffice.entity.Horaire> horaires);
    
    // Get all users
    public List<User> findAll();
    
    // Update and delete user
    public void updateUser(User user);
    public void deleteUser(int id);
    
    // Stats
    public long countPatients();
    public long countDentistes();
    public long countDistinctGouvernorats();
}
