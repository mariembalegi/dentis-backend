package com.enit.backoffice.controllers;

import com.enit.backoffice.dao.IRendezvousDAO;
import com.enit.backoffice.dao.IServiceMedicalDAO;
import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.dto.RendezvousDTO;
import com.enit.backoffice.entity.*;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/rendezvous")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RendezvousRest {

    @EJB IRendezvousDAO rvDAO;
    @EJB IUserDAO userDAO;
    @EJB IServiceMedicalDAO serviceDAO;

    private RendezvousDTO mapToDTO(Rendezvous r) {
        RendezvousDTO dto = new RendezvousDTO();
        dto.setIdRv(r.getIdRv());
        dto.setDateRv(r.getDateRv());
        dto.setHeureRv(r.getHeureRv());
        dto.setStatutRv(r.getStatutRv());
        dto.setDescriptionRv(r.getDescriptionRv());
        dto.setPatientId(r.getPatient().getId());
        dto.setDentistId(r.getDentiste().getId());
        dto.setPatientName(r.getPatient().getNom() + " " + r.getPatient().getPrenom());
        dto.setDentistName(r.getDentiste().getNom() + " " + r.getDentiste().getPrenom());
        if(r.getServiceMedical() != null) {
            dto.setServiceId(r.getServiceMedical().getNumSM());
            dto.setServiceName(r.getServiceMedical().getNomSM());
        }
        return dto;
    }

    @POST
    public Response bookRendezvous(RendezvousDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Patient)) return Response.status(Response.Status.FORBIDDEN).entity("Only patients can book").build();

        Patient patient = (Patient) user;
        Dentiste dentiste = (Dentiste) userDAO.findById(dto.getDentistId());
        if(dentiste == null) return Response.status(Response.Status.BAD_REQUEST).entity("Dentist not found").build();
        
        ServiceMedical service = null;
        if(dto.getServiceId() != null) {
             service = serviceDAO.findById(dto.getServiceId());
        }

        Rendezvous rv = new Rendezvous();
        rv.setPatient(patient);
        rv.setDentiste(dentiste);
        rv.setServiceMedical(service);
        rv.setDateRv(dto.getDateRv());
        rv.setHeureRv(dto.getHeureRv());
        rv.setDescriptionRv(dto.getDescriptionRv());
        rv.setStatutRv("PENDING"); // Default status

        rvDAO.addRendezvous(rv);
        return Response.ok("Rendezvous booked successfully").build();
    }

    @GET
    @Path("/my")
    public Response getMyRendezvous(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        List<Rendezvous> rvs;
        if (user instanceof Dentiste) {
            rvs = rvDAO.findByDentistId(user.getId());
        } else if (user instanceof Patient) {
            rvs = rvDAO.findByPatientId(user.getId());
        } else {
             return Response.ok(List.of()).build();
        }
        
        return Response.ok(rvs.stream().map(this::mapToDTO).collect(Collectors.toList())).build();
    }

    @PUT
    @Path("/{id}/validate")
    public Response validateRendezvous(@PathParam("id") Integer id, @QueryParam("status") String status, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        if (!(user instanceof Dentiste)) return Response.status(Response.Status.FORBIDDEN).build();
        
        Rendezvous rv = rvDAO.findById(id);
        if(rv == null) return Response.status(Response.Status.NOT_FOUND).build();
        
        if(rv.getDentiste().getId() != user.getId()) return Response.status(Response.Status.FORBIDDEN).build();

        if("VALIDATED".equalsIgnoreCase(status) || "REFUSED".equalsIgnoreCase(status)) {
            rv.setStatutRv(status.toUpperCase());
            rvDAO.updateRendezvous(rv);
            return Response.ok("Status updated").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status").build();
    }
    
    @PUT
    @Path("/{id}/cancel") // Or modify
    public Response updateRendezvousByPatient(@PathParam("id") Integer id, RendezvousDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Patient)) return Response.status(Response.Status.FORBIDDEN).build();

        Rendezvous rv = rvDAO.findById(id);
        if(rv == null) return Response.status(Response.Status.NOT_FOUND).build();
        if(rv.getPatient().getId() != user.getId()) return Response.status(Response.Status.FORBIDDEN).build();

        if(!"PENDING".equalsIgnoreCase(rv.getStatutRv())) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Cannot modify validated/refused rendezvous").build();
        }
        
        // Update fields if provided
        if(dto.getDateRv() != null) rv.setDateRv(dto.getDateRv());
        if(dto.getHeureRv() != null) rv.setHeureRv(dto.getHeureRv());
        if(dto.getDescriptionRv() != null) rv.setDescriptionRv(dto.getDescriptionRv());
        // Can cancel by setting status? Prompt says "cancel or modify".
        // Let's assume cancel is via a status update or DELETE. But prompt says "cancel... if not validated".
        if(dto.getStatutRv() != null && "CANCELLED".equalsIgnoreCase(dto.getStatutRv())) {
             rv.setStatutRv("CANCELLED");
        }

        rvDAO.updateRendezvous(rv);
        return Response.ok("Rendezvous updated").build();
    }
}
