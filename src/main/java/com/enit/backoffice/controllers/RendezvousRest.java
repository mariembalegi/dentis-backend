package com.enit.backoffice.controllers;

import com.enit.backoffice.dao.IRendezvousDAO;
import com.enit.backoffice.dao.IServiceMedicalDAO;
import com.enit.backoffice.dao.IUserDAO;
import com.enit.backoffice.dao.IActeMedicalDAO; // New import
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

@Path("/rendezvousREST")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RendezvousRest {

    @EJB IRendezvousDAO rvDAO;
    @EJB IUserDAO userDAO;
    @EJB IServiceMedicalDAO serviceDAO;
    @EJB IActeMedicalDAO acteDAO; // New EJB

    private RendezvousDTO mapToDTO(Rendezvous r) {
        RendezvousDTO dto = new RendezvousDTO();
        dto.setIdRv(r.getIdRv());
        dto.setDateRv(r.getDateRv());
        dto.setHeureRv(r.getHeureRv());
        dto.setStatutRv(r.getStatutRv().name());
        dto.setDescriptionRv(r.getDescriptionRv());
        if(r.getPatient() != null) {
            dto.setPatientId(r.getPatient().getId());
            dto.setPatientName(r.getPatient().getNom() + " " + r.getPatient().getPrenom());
        }
        dto.setDentistId(r.getDentiste().getId());
        dto.setDentistName(r.getDentiste().getNom() + " " + r.getDentiste().getPrenom());
        
        // Show Acts info if any (using the first one for display if user selected one)
        if(r.getActes() != null && !r.getActes().isEmpty()) {
             ActeMedical firstActe = r.getActes().get(0);
             dto.setServiceId(firstActe.getServiceMedical().getNumSM());
             dto.setServiceName(firstActe.getServiceMedical().getNomSM());
        }
        return dto;
    }

    @POST
    @Path("/add")
    public Response addRendezvous(RendezvousDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        if (!(user instanceof Dentiste)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Seul le dentiste peut créer un créneau de rendez-vous").build();
        }

        // Vérification des champs obligatoires
        if(dto.getDateRv() == null || dto.getHeureRv() == null) {
             return Response.status(Response.Status.BAD_REQUEST).entity("Date et Heure sont obligatoires").build();
        }

        Rendezvous rv = new Rendezvous();
        rv.setDateRv(dto.getDateRv());
        rv.setHeureRv(dto.getHeureRv());
        rv.setDescriptionRv(dto.getDescriptionRv()); // Facultatif

        rv.setPatient(null);
        rv.setDentiste((Dentiste) user);
        rv.setStatutRv(StatutRendezvous.DISPONIBLE);
        
        rvDAO.addRendezvous(rv);
        
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Créneau de rendez-vous ajouté avec succès");
            put("id", rv.getIdRv());
        }}).build();
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

        if("VALIDATED".equalsIgnoreCase(status)) {
            // Confirmation : reste NON_DISPONIBLE
            rv.setStatutRv(StatutRendezvous.NON_DISPONIBLE);
        } else if ("REFUSED".equalsIgnoreCase(status)) {
            // Refus : Libère le créneau
            rv.setStatutRv(StatutRendezvous.DISPONIBLE);
            rv.setPatient(null);
        } else {
             return Response.status(Response.Status.BAD_REQUEST).entity(new java.util.HashMap<String, Object>() {{
                put("error", "Invalid status");
             }}).build();
        }
        
        rvDAO.updateRendezvous(rv);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Status updated");
            put("status", rv.getStatutRv().name());
        }}).build();
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

        // Le patient peut modifier son RDV
        
        // Update fields if provided
        if(dto.getDateRv() != null) rv.setDateRv(dto.getDateRv());
        if(dto.getHeureRv() != null) rv.setHeureRv(dto.getHeureRv());
        if(dto.getDescriptionRv() != null) rv.setDescriptionRv(dto.getDescriptionRv());
        
        // Annulation par le patient -> Libère le créneau
        if(dto.getStatutRv() != null && "CANCELLED".equalsIgnoreCase(dto.getStatutRv())) {
             rv.setStatutRv(StatutRendezvous.DISPONIBLE);
             rv.setPatient(null);
        }

        rvDAO.updateRendezvous(rv);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Rendezvous updated");
            put("id", rv.getIdRv());
        }}).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRendezvous(@PathParam("id") int id, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        Rendezvous rv = rvDAO.findById(id);
        if (rv == null) return Response.status(Response.Status.NOT_FOUND).build();

        // Le patient ou le dentiste concerné peuvent supprimer, MAIS PAS l'Admin
        boolean isPatientOwner = (user instanceof Patient) && (rv.getPatient().getId() == user.getId());
        boolean isDentistOwner = (user instanceof Dentiste) && (rv.getDentiste().getId() == user.getId());

        if (!isPatientOwner && !isDentistOwner) {
            return Response.status(Response.Status.FORBIDDEN).entity(new java.util.HashMap<String, Object>() {{
                put("error", "Only the patient or dentist involved can delete this appointment");
            }}).build();
        }

        rvDAO.deleteRendezvous(id);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Rendezvous supprimé");
        }}).build();
    }
}
