package com.enit.backoffice.dto;
import java.time.LocalDate;
import java.time.LocalTime;

public class RendezvousDTO {
    private Integer idRv;
    private int patientId;
    private int dentistId;
    private Integer serviceId;
    private LocalDate dateRv;
    private LocalTime heureRv;
    private String statutRv;
    private String descriptionRv;
    
    // Helpers for display
    private String patientName;
    private String dentistName;
    private String serviceName;

    public Integer getIdRv() { return idRv; }
    public void setIdRv(Integer idRv) { this.idRv = idRv; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public int getDentistId() { return dentistId; }
    public void setDentistId(int dentistId) { this.dentistId = dentistId; }
    public Integer getServiceId() { return serviceId; }
    public void setServiceId(Integer serviceId) { this.serviceId = serviceId; }
    public LocalDate getDateRv() { return dateRv; }
    public void setDateRv(LocalDate dateRv) { this.dateRv = dateRv; }
    public LocalTime getHeureRv() { return heureRv; }
    public void setHeureRv(LocalTime heureRv) { this.heureRv = heureRv; }
    public String getStatutRv() { return statutRv; }
    public void setStatutRv(String statutRv) { this.statutRv = statutRv; }
    public String getDescriptionRv() { return descriptionRv; }
    public void setDescriptionRv(String descriptionRv) { this.descriptionRv = descriptionRv; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getDentistName() { return dentistName; }
    public void setDentistName(String dentistName) { this.dentistName = dentistName; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
}
