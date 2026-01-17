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
}
