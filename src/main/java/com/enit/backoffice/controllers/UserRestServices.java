package com.enit.backoffice.webservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.HashMap;

import org.mindrot.jbcrypt.BCrypt;

import com.enit.backoffice.DTO.LoginAdminResponseDTO;
import com.enit.backoffice.DTO.LoginDentisteResponseDTO;
import com.enit.backoffice.DTO.LoginPatientResponseDTO;
import com.enit.backoffice.DTO.LoginUserResponseDTO;
import com.enit.backoffice.business.IUserDAO;
import com.enit.backoffice.entity.Dentiste;
import com.enit.backoffice.entity.Patient;
import com.enit.backoffice.entity.Admin;
import com.enit.backoffice.entity.User;

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

	  public Response login(User user) {
		  HashMap<String, Object> response = new HashMap<>();
		  User userFromDB = userDAO.findByEmail(user.getEmail());
		  String role;
		  LoginUserResponseDTO dto;
		  
		  boolean passwordMatches = BCrypt.checkpw(user.getMotDePasse(), userFromDB.getMotDePasse());
		  if (!passwordMatches) {
		      response.put("message", "Password incorrect!");
		      return Response.status(Response.Status.UNAUTHORIZED).entity(response).build();
		  }

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

          return Response.ok(response).build();
	  }
}
