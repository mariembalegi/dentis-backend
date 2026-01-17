package com.enit.backoffice.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;

import org.mindrot.jbcrypt.BCrypt;

import com.enit.backoffice.dto.LoginAdminResponseDTO;
import com.enit.backoffice.dto.LoginDentisteResponseDTO;
import com.enit.backoffice.dto.LoginPatientResponseDTO;
import com.enit.backoffice.dto.LoginUserResponseDTO;
import com.enit.backoffice.dao.IUserDAO; // Modified import
import com.enit.backoffice.dto.SignupPatientRequestDTO;
import com.enit.backoffice.dto.SignupDentisteRequestDTO;
import com.enit.backoffice.entity.Dentiste;
import com.enit.backoffice.entity.Patient;
import com.enit.backoffice.entity.Admin;
import com.enit.backoffice.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



@Stateless
@Path("/userREST")
public class UserRestServices {
	
	@EJB
	IUserDAO userDAO;
    
    @EJB
    com.enit.backoffice.dao.IServiceMedicalDAO serviceDAO;
	
     
	  @GET
	  @Path("/check-email/{email}")
	  @Produces(MediaType.APPLICATION_JSON) 
	    public Response checkEmail(@PathParam("email") String email) {

	        if (userDAO.existsByEmail(email)) {
            return Response.ok(new java.util.HashMap<String, Object>() {{
                put("exists", true);
                put("email", email);
            }}).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new java.util.HashMap<String, Object>() {{
                               put("exists", false);
                               put("message", "Email n'existe pas");
                           }})
	                           .build();
	        }
	    }
	  
	  @POST
	  @Path("/login")
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)
	  public Response login(com.enit.backoffice.dto.LoginRequestDTO loginRequest, @Context HttpServletRequest req) {
		  HashMap<String, Object> response = new HashMap<>();
		  System.out.println("Login attempt for: " + loginRequest.getEmail());
		  
		  if (loginRequest.getEmail() == null || loginRequest.getMotDePasse() == null) {
			  response.put("error", "Email and password are required");
			  return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
		  }

		  User userFromDB = userDAO.findByEmail(loginRequest.getEmail());
                   if(userFromDB == null) {
                	   System.out.println("User not found: " + loginRequest.getEmail());
                       return Response.status(Response.Status.UNAUTHORIZED).entity(new java.util.HashMap<String, Object>() {{
                           put("error", "User not found");
                       }}).build();
                   }
		  String role;
		  LoginUserResponseDTO dto;
		  
		  boolean passwordMatches = BCrypt.checkpw(loginRequest.getMotDePasse(), userFromDB.getMotDePasse());
		  if (!passwordMatches) {
			  System.out.println("Password mismatch for: " + loginRequest.getEmail());
		      response.put("message", "Password incorrect!");
		      return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
		  }

                    // Create Session
                   HttpSession session = req.getSession(true);
                   session.setAttribute("user", userFromDB);

          if (userFromDB instanceof Patient patient) {
            role = "PATIENT";
            LoginPatientResponseDTO pDto = new LoginPatientResponseDTO();
            pDto.setDateNaissanceP(patient.getDateNaissanceP());
            pDto.setGroupeSanguinP(patient.getGroupeSanguinP() != null ? patient.getGroupeSanguinP().toString() : null);
            pDto.setRecouvrementP(patient.getRecouvrementP() != null ? patient.getRecouvrementP().getLabel() : null);
            dto = pDto;

          } else if (userFromDB instanceof Dentiste dentiste) {
            role = "DENTISTE";
            LoginDentisteResponseDTO dDto = new LoginDentisteResponseDTO();
            // Removed: dDto.setSpecialiteD(dentiste.getSpecialiteD());
            dto = dDto;
	      }
	     
	       else {
	    	   Admin admin = (Admin) userFromDB; 
	    	   role = "ADMIN";
	    	   LoginAdminResponseDTO aDto = new LoginAdminResponseDTO();
	    	   aDto.setAdminType(admin.getAdminType());
	    	   dto=aDto;
	      }
		
          // Use UTF-8 for consistency
          Key key = Keys.hmacShaKeyFor("MaCleSuperSecrete1234567890123456".getBytes(StandardCharsets.UTF_8));

          String token = Jwts.builder()
                  .setSubject(userFromDB.getEmail())
                  .claim("role", role)
                  .signWith(key) // plus de String direct
                  .compact();

          dto.setId(userFromDB.getId());
          dto.setNom(userFromDB.getNom());
          dto.setPrenom(userFromDB.getPrenom());
          dto.setEmail(userFromDB.getEmail());
          dto.setRole(role);
          dto.setTel(userFromDB.getTel());
          if (userFromDB.getSexe() != null) {
              dto.setSexe(userFromDB.getSexe().toString());
          }
          dto.setPhoto(userFromDB.getPhoto());
          dto.setToken(token);

          response.put("user", dto);
          response.put("message", "Login successful");
          response.put("sessionId", session.getId()); // Return Session ID

          return Response.ok(response).build();
	  }

      @GET
      @Path("/logout")
      @Produces(MediaType.APPLICATION_JSON)
      public Response logout(@Context HttpServletRequest req) {
          HttpSession session = req.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "Logged out");
          }}).build();
      }

      @POST
      @Path("/signup/patient")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Response signupPatient(SignupPatientRequestDTO dto) {
          if (userDAO.existsByEmail(dto.getEmail())) {
              return Response.status(Response.Status.BAD_REQUEST).entity(new java.util.HashMap<String, Object>() {{
                  put("error", "Email exists");
              }}).build();
          }
          Patient p = new Patient();
          p.setNom(dto.getNom());
          p.setPrenom(dto.getPrenom());
          p.setEmail(dto.getEmail());
          p.setMotDePasse(dto.getMotDePasse());
          p.setTel(dto.getTel());
          if (dto.getSexe() != null) {
              p.setSexe(com.enit.backoffice.entity.Sexe.valueOf(dto.getSexe()));
          }
          p.setPhoto(dto.getPhoto());
          p.setDateNaissanceP(dto.getDateNaissanceP());
          if (dto.getGroupeSanguinP() != null) {
              p.setGroupeSanguinP(com.enit.backoffice.entity.GroupeSanguin.valueOf(dto.getGroupeSanguinP()));
          }
          if (dto.getRecouvrementP() != null) {
              p.setRecouvrementP(com.enit.backoffice.entity.TypeRecouvrement.fromLabel(dto.getRecouvrementP()));
          }
          
          userDAO.addUser(p);
          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "Patient registered");
              put("id", p.getId());
          }}).build();
      }

      @POST
      @Path("/signup/dentist")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Response signupDentist(SignupDentisteRequestDTO dto) {
          if (userDAO.existsByEmail(dto.getEmail())) {
              return Response.status(Response.Status.BAD_REQUEST).entity(new java.util.HashMap<String, Object>() {{
                  put("error", "Email exists");
              }}).build();
          }
          Dentiste d = new Dentiste();
          d.setNom(dto.getNom());
          d.setPrenom(dto.getPrenom());
          d.setEmail(dto.getEmail());
          d.setMotDePasse(dto.getMotDePasse());
          d.setTel(dto.getTel());
          if (dto.getSexe() != null) {
              d.setSexe(com.enit.backoffice.entity.Sexe.valueOf(dto.getSexe()));
          }
          d.setPhoto(dto.getPhoto());
          d.setDiplome(dto.getDiplome());
          d.setSpecialite(dto.getSpecialite());
          d.setGouvernorat(dto.getGouvernorat());
          d.setDelegation(dto.getDelegation());
          d.setAdresse(dto.getAdresse());
          
          userDAO.addUser(d);
          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "Dentist registered");
              put("id", d.getId());
          }}).build();
      }
      
      @GET
      @Path("/patients")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getAllPatients() {
    	  return Response.ok(userDAO.getAllPatients()).build();
      }
}