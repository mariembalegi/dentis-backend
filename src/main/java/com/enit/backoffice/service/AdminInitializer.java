package com.enit.backoffice.service;

import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.entity.Admin;
import com.enit.backoffice.entity.Sexe;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.ejb.EJB;

import org.mindrot.jbcrypt.BCrypt;

@Singleton
@Startup
public class AdminInitializer {

    @EJB
    private IUserDAO userDAO;

    private static final String ADMIN_EMAIL = "admin@dentis.com";
    private static final String ADMIN_PASSWORD = "root";

    @PostConstruct
    public void init() {
        try {
            // Check if admin already exists
            if (!userDAO.existsByEmail(ADMIN_EMAIL)) {
                Admin admin = new Admin();
                admin.setNom("Admin");
                admin.setPrenom("Dentis");
                admin.setEmail(ADMIN_EMAIL);
                // Don't hash here - addUser() already hashes the password
                admin.setMotDePasse(ADMIN_PASSWORD);
                admin.setTel(0);
                admin.setSexe(Sexe.M);
                admin.setAdminType("SUPER_ADMIN");

                userDAO.addUser(admin);
                System.out.println("✅ Admin created: " + ADMIN_EMAIL + " / " + ADMIN_PASSWORD);
            } else {
                System.out.println("ℹ️ Admin already exists: " + ADMIN_EMAIL);
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating admin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
