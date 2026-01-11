package com.enit.backoffice.controllers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.HashMap;

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
	
     
	  @GET
	  @Path("/check-email/{email}")
	  @Produces(MediaType.APPLICATION_JSON) 
	    public Response checkEmail(@PathParam("email") String email) {

	        if (userDAO.existsByEmail(email)) {
	            return Response.ok().build(); // 200 OK
	        } else {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("Email n'existe pas")
	                           .build();
	        }
	    }
	  
	  @POST
	  @Path("/login")
	  @Consumes(MediaType.APPLICATION_JSON)
	  @Produces(MediaType.APPLICATION_JSON)

	  public Response login(User user, @Context HttpServletRequest req) {
		  HashMap<String, Object> response = new HashMap<>();
		  User userFromDB = userDAO.findByEmail(user.getEmail());
                   if(userFromDB == null) {
                       return Response.status(Response.Status.UNAUTHORIZED).entity("User not found").build();
                   }
		  String role;
		  LoginUserResponseDTO dto;
		  
		  boolean passwordMatches = BCrypt.checkpw(user.getMotDePasse(), userFromDB.getMotDePasse());
		  if (!passwordMatches) {
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
            pDto.setGroupeSanguinP(patient.getGroupeSanguinP());
            pDto.setRecouvrementP(patient.getRecouvrementP());
            dto = pDto;

          } else if (userFromDB instanceof Dentiste dentiste) {
            role = "DENTISTE";
            LoginDentisteResponseDTO dDto = new LoginDentisteResponseDTO();
            dDto.setSpecialiteD(dentiste.getSpecialiteD());
            dto = dDto;
	      }
	     
	       else {
	    	   Admin admin = (Admin) userFromDB; 
	    	   role = "ADMIN";
	    	   LoginAdminResponseDTO aDto = new LoginAdminResponseDTO();
	    	   aDto.setAdminType(admin.getAdminType());
	    	   dto=aDto;
	      }
		
          Key key = Keys.hmacShaKeyFor("MaCleSuperSecrete1234567890123456".getBytes());

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
          dto.setToken(token);

          response.put("user", dto);
          response.put("message", "Login successful");
          response.put("sessionId", session.getId()); // Return Session ID

          return Response.ok(response).build();
	  }

      @GET
      @Path("/logout")
      public Response logout(@Context HttpServletRequest req) {
          HttpSession session = req.getSession(false);
          if (session != null) {
              session.invalidate();
          }
          return Response.ok("Logged out").build();
      }

      @POST
      @Path("/signup/patient")
      @Consumes(MediaType.APPLICATION_JSON)
      public Response signupPatient(SignupPatientRequestDTO dto) {
          if (userDAO.existsByEmail(dto.getEmail())) {
              return Response.status(Response.Status.BAD_REQUEST).entity("Email exists").build();
          }
          Patient p = new Patient();
          p.setNom(dto.getNom());
          p.setPrenom(dto.getPrenom());
          p.setEmail(dto.getEmail());
          p.setMotDePasse(dto.getMotDePasse());
          p.setTel(dto.getTel());
          p.setSexe(dto.getSexe());
          p.setPhoto(dto.getPhoto());
          p.setDateNaissanceP(dto.getDateNaissanceP());
          p.setGroupeSanguinP(dto.getGroupeSanguinP());
          p.setRecouvrementP(dto.getRecouvrementP());
          
          userDAO.addUser(p);
          return Response.ok("Patient registered").build();
      }

      @POST
      @Path("/signup/dentist")
      @Consumes(MediaType.APPLICATION_JSON)
      public Response signupDentist(SignupDentisteRequestDTO dto) {
          if (userDAO.existsByEmail(dto.getEmail())) {
              return Response.status(Response.Status.BAD_REQUEST).entity("Email exists").build();
          }
          Dentiste d = new Dentiste();
          d.setNom(dto.getNom());
          d.setPrenom(dto.getPrenom());
          d.setEmail(dto.getEmail());
          d.setMotDePasse(dto.getMotDePasse());
          d.setTel(dto.getTel());
          d.setSexe(dto.getSexe());
          d.setPhoto(dto.getPhoto());
          d.setSpecialiteD(dto.getSpecialiteD());
          d.setDiplome(dto.getDiplome());
          
          userDAO.addUser(d);
          return Response.ok("Dentist registered").build();
      }
}
