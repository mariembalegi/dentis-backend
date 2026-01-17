package com.enit.backoffice.controllers;

import com.enit.backoffice.dao.IPublicationDAO;
import com.enit.backoffice.dto.PublicationDTO;
import com.enit.backoffice.entity.Admin;
import com.enit.backoffice.entity.Dentiste;
import com.enit.backoffice.entity.Publication;
import com.enit.backoffice.entity.User;

import jakarta.ejb.EJB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/publicationRest")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PublicationRest {

    @EJB
    IPublicationDAO publicationDAO;

    private PublicationDTO mapToDTO(Publication p) {
        PublicationDTO dto = new PublicationDTO();
        dto.setIdPub(p.getIdPub());
        dto.setTitrePub(p.getTitrePub());
        dto.setDatePub(p.getDatePub());
        dto.setTypePub(p.getTypePub() != null ? p.getTypePub().getLabel() : null);
        dto.setDescription(p.getDescription());
        dto.setFichierPub(p.getFichierPub());
        dto.setAffichePub(p.getAffichePub());
        dto.setValide(p.isValide());
        if (p.getDentiste() != null) {
            dto.setDentistId(p.getDentiste().getId());
            dto.setDentistName(p.getDentiste().getNom() + " " + p.getDentiste().getPrenom());
        }
        return dto;
    }

    // 1. Ajouter une publication (Dentiste uniquement) -> valide = false
    @POST
    @Path("/add")
    public Response addPublication(PublicationDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Dentiste)) {
            return Response.status(Response.Status.FORBIDDEN).entity("Seul un dentiste peut publier").build();
        }

        Publication p = new Publication();
        p.setTitrePub(dto.getTitrePub());
        p.setDescription(dto.getDescription());
        if (dto.getTypePub() != null) {
            p.setTypePub(com.enit.backoffice.entity.TypePublication.fromLabel(dto.getTypePub()));
        }
        p.setDatePub(new Date()); // Date du jour
        p.setFichierPub(dto.getFichierPub());
        p.setAffichePub(dto.getAffichePub());
        p.setValide(false); // Par défaut non valide
        p.setDentiste((Dentiste) user);  

        publicationDAO.addPublication(p);
        
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Publication ajoutée et en attente de validation");
            put("id", p.getIdPub());
        }}).build();
    }

    // 2. Récupérer toutes les publications validées (Pour tout le monde)
    @GET
    @Path("/publications")
    public Response getAllValidPublications() {
        List<PublicationDTO> list = publicationDAO.findAllValid().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return Response.ok(list).build();
    }

    // 3. Récupérer les publications en attente (Admin ou Dentiste pour ses propres publications)
    @GET
    @Path("/pending")
    public Response getPendingPublications(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        
        User user = (User) session.getAttribute("user");
        
        List<PublicationDTO> list;
        
        if (user instanceof Admin) {
             // Admin voit toutes les publications en attente
             list = publicationDAO.findAllPending().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        } else if (user instanceof Dentiste) {
             // Dentiste voit seulement ses propres publications en attente
             list = publicationDAO.findAllPending().stream()
                .filter(p -> p.getDentiste().getId() == user.getId())
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        } else {
             return Response.status(Response.Status.FORBIDDEN).build();
        }

        return Response.ok(list).build();
    }

    // 4. Valider une publication (Admin uniquement)
    @PUT
    @Path("/{id}/validate")
    public Response validatePublication(@PathParam("id") int id, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Admin)) {
             return Response.status(Response.Status.FORBIDDEN).build();
        }

        Publication p = publicationDAO.findById(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        p.setValide(true);
        publicationDAO.updatePublication(p);

        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Publication validée");
        }}).build();
    }

    // 8. Invalider / Dépublier une publication (Admin uniquement)
    @PUT
    @Path("/{id}/invalidate")
    public Response invalidatePublication(@PathParam("id") int id, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        
        User user = (User) session.getAttribute("user");
        if (!(user instanceof Admin)) {
             return Response.status(Response.Status.FORBIDDEN).build();
        }

        Publication p = publicationDAO.findById(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        p.setValide(false);
        publicationDAO.updatePublication(p);

        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Publication dépubliée (remise en attente)");
        }}).build();
    }

    // 5. Rejeter / Supprimer une publication (Admin ou Propriétaire)
    @DELETE
    @Path("/{id}")
    public Response deletePublication(@PathParam("id") int id, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        Publication p = publicationDAO.findById(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        // Admin peut tout supprimer. Dentiste ne peut supprimer que SES publications.
        boolean isAdmin = (user instanceof Admin);
        boolean isOwner = (user instanceof Dentiste) && (p.getDentiste().getId() == user.getId());

        if (!isAdmin && !isOwner) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        publicationDAO.deletePublication(id);
        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Publication supprimée");
        }}).build();
    }

    // 7. Modifier une publication (Dentiste) -> redevient non valide
    @PUT
    @Path("/{id}")
    public Response updatePublication(@PathParam("id") int id, PublicationDTO dto, @Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");

        Publication p = publicationDAO.findById(id);
        if (p == null) return Response.status(Response.Status.NOT_FOUND).build();

        // Seul le propriétaire peut modifier
        if (!(user instanceof Dentiste) || p.getDentiste().getId() != user.getId()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Vous ne pouvez modifier que vos publications").build();
        }

        // Mise à jour des champs
        if(dto.getTitrePub() != null) p.setTitrePub(dto.getTitrePub());
        if(dto.getDescription() != null) p.setDescription(dto.getDescription());
        if(dto.getFichierPub() != null) p.setFichierPub(dto.getFichierPub());
        if(dto.getAffichePub() != null) p.setAffichePub(dto.getAffichePub());
        if(dto.getTypePub() != null) {
             p.setTypePub(com.enit.backoffice.entity.TypePublication.fromLabel(dto.getTypePub()));
        }
        
        // IMPORTANT: Toute modification nécessite une nouvelle validation
        p.setValide(false);
        
        publicationDAO.updatePublication(p);

        return Response.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Publication modifiée et remise en attente de validation");
            put("id", p.getIdPub());
        }}).build();
    }

    // 6. Mes publications (Pour le dentiste connecte)
    @GET
    @Path("/my")
    public Response getMyPublications(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        User user = (User) session.getAttribute("user");
        
        if (!(user instanceof Dentiste)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        
        List<PublicationDTO> list = publicationDAO.findByDentistId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return Response.ok(list).build();
    }
}
