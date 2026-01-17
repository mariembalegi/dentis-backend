package com.enit.backoffice.dto;

import java.util.List;

public class DentisteDetailsDTO extends DentisteSearchDTO {

    private List<ServiceMedicalDTO> services;
    private List<RendezvousDTO> availableRendezvous;

    public DentisteDetailsDTO() {
        super();
    }

    // Constructor copying fields from DentisteSearchDTO
    public DentisteDetailsDTO(DentisteSearchDTO base) {
        super(base.getId(), base.getNom(), base.getPrenom(), base.getGouvernorat(), 
              base.getDelegation(), base.getAdresse(), base.getDiplome(), base.getSpecialite());
        if(base.getPhoto() != null) {
            this.setPhoto(base.getPhoto());
        }
    }

    public List<ServiceMedicalDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceMedicalDTO> services) {
        this.services = services;
    }

    public List<RendezvousDTO> getAvailableRendezvous() {
        return availableRendezvous;
    }

    public void setAvailableRendezvous(List<RendezvousDTO> availableRendezvous) {
        this.availableRendezvous = availableRendezvous;
    }
}
