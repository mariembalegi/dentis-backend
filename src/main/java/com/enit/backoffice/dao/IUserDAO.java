package com.enit.backoffice.dao;



import com.enit.backoffice.entity.User;

import jakarta.ejb.Local;

@Local
public interface IUserDAO {
	
	 // Récupérer utilisateur par email 
    public User findByEmail(String email);

    
 // Vérifier l’existence d’un email 
    public boolean existsByEmail(String email);

    
    // Ajouter User
    public void addUser(User user);
}
