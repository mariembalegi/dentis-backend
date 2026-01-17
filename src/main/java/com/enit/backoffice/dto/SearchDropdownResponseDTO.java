package com.enit.backoffice.dto;

import java.util.List;

public class SearchDropdownResponseDTO {
    private List<String> services;
    private List<DentisteSearchDTO> dentistes;

    public SearchDropdownResponseDTO(List<String> services, List<DentisteSearchDTO> dentistes) {
        this.services = services;
        this.dentistes = dentistes;
    }

    public SearchDropdownResponseDTO() {}

    public List<String> getServices() { return services; }
    public void setServices(List<String> services) { this.services = services; }
    public List<DentisteSearchDTO> getDentistes() { return dentistes; }
    public void setDentistes(List<DentisteSearchDTO> dentistes) { this.dentistes = dentistes; }
}
