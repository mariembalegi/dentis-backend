package com.enit.backoffice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "admin")
public class Admin extends User {

	private static final long serialVersionUID = 1L;
	
	private String adminType;

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }
}