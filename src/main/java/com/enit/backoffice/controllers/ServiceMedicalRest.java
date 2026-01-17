package com.enit.backoffice.controllers;

import com.enit.backoffice.dao.IServiceMedicalDAO;
import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.dto.ServiceMedicalDTO;
import com.enit.backoffice.entity.Dentiste;
import com.enit.backoffice.entity.ServiceMedical;
import com.enit.backoffice.entity.User;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServiceMedicalRest {

    @EJB
    IServiceMedicalDAO serviceDAO;
    
    @EJB
    IUserDAO userDAO;

    // Helper to map Entity to DTO
    private ServiceMedicalDTO mapToDTO(ServiceMedical s) {
        ServiceMedicalDTO dto = new ServiceMedicalDTO();
        dto.setNumSM(s.getNumSM());
        dto.setNomSM(s.getNomSM());
        dto.setTypeSM(s.getTypeSM() != null ? s.getTypeSM().getLabel() : null);
        dto.setDescriptionSM(s.getDescriptionSM());
        dto.setTarifSM(s.getTarifSM());
        dto.setImage(s.getImage());
        if(s.getDentiste() != null) {
            dto.setDentistId(s.getDentiste().getId());
        }
        return dto;
    }

    @POST
    public Response addService(ServiceMedicalDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(new java.util.HashMap<String, Object>() {{
                put("error", "User not logged in");
            }}).build();
        }
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Dentiste)) {
            return Response.status(Response.Status.FORBIDDEN).entity(new java.util.HashMap<String, Object>() {{
                put("error", "Only dentists can add services");
            }}).build();
        }

        Dentiste dentiste = (Dentiste) user;
        ServiceMedical service = new ServiceMedical();
        service.setNomSM(dto.getNomSM());
        if (dto.getTypeSM() != null) {
            service.setTypeSM(com.enit.backoffice.entity.TypeServiceMedical.fromLabel(dto.getTypeSM()));
        }
        service.setDescriptionSM(dto.getDescriptionSM());
        service.setTarifSM(dto.getTarifSM());
        service.setImage(dto.getImage()); // Base64
        service.setDentiste(dentiste);

        serviceDAO.addService(service);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Service added successfully");
            put("id", service.getNumSM());
        }}).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateService(@PathParam("id") Integer id, ServiceMedicalDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        
        ServiceMedical service = serviceDAO.findById(id);
        if (service == null) return Response.status(Response.Status.NOT_FOUND).build();

        User user = (User) session.getAttribute("user");
        if(user.getId() != service.getDentiste().getId()) {
             return Response.status(Response.Status.FORBIDDEN).build();
        }

        service.setNomSM(dto.getNomSM());
        if (dto.getTypeSM() != null) {
            service.setTypeSM(com.enit.backoffice.entity.TypeServiceMedical.fromLabel(dto.getTypeSM()));
        }
        service.setDescriptionSM(dto.getDescriptionSM());
        service.setTarifSM(dto.getTarifSM());
        service.setImage(dto.getImage());
        
        serviceDAO.updateService(service);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Service updated");
            put("id", service.getNumSM());
        }}).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteService(@PathParam("id") Integer id, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        
        ServiceMedical service = serviceDAO.findById(id);
        if (service == null) return Response.status(Response.Status.NOT_FOUND).build();

        User user = (User) session.getAttribute("user");
        if(user.getId() != service.getDentiste().getId()) {
             return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        serviceDAO.deleteService(id);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Service deleted");
            put("id", id);
        }}).build();
    }

    @GET
    public Response getAllServices() {
        List<ServiceMedicalDTO> dtos = serviceDAO.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
    
    @GET
    @Path("/dentist/me")
    public Response getMyServices(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");
        
        List<ServiceMedicalDTO> dtos = serviceDAO.findByDentistId(user.getId()).stream().map(this::mapToDTO).collect(Collectors.toList());
        return Response.ok(dtos).build();
    }
}
