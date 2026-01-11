package com.enit.backoffice.dto;

public class LoginAdminResponseDTO extends LoginUserResponseDTO {
    private String adminType;

    public String getAdminType() {
        return adminType;
    }

    public void setAdminType(String adminType) {
        this.adminType = adminType;
    }
}