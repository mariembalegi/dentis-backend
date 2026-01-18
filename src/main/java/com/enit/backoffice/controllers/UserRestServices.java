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
import com.enit.backoffice.dto.DentisteSearchDTO;
// import com.enit.backoffice.dto.DentisteDetailsDTO;
import com.enit.backoffice.dto.ServiceMedicalDTO;
import com.enit.backoffice.dto.RendezvousDTO;
import com.enit.backoffice.dto.HoraireDTO;
import com.enit.backoffice.dao.IUserDAO; // Modified import
import com.enit.backoffice.dto.SignupPatientRequestDTO;
import com.enit.backoffice.dto.SignupDentisteRequestDTO;
import com.enit.backoffice.entity.Dentiste;
import com.enit.backoffice.entity.Patient;
import com.enit.backoffice.entity.Admin;
import com.enit.backoffice.entity.User;

import com.enit.backoffice.entity.ServiceMedical;
import com.enit.backoffice.entity.Rendezvous;
import com.enit.backoffice.entity.Horaire;
import com.enit.backoffice.entity.JourSemaine;
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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

@Stateless
@Path("/userREST")
public class UserRestServices {
	
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

	private LocalTime parseTime(String timeStr) {
		if (timeStr == null || timeStr.trim().isEmpty()) {
			return null;
		}
		try {
            // Support "HH:mm:ss" coming from DB or "HH:mm" from some inputs
            if (timeStr.length() > 5) {
               return LocalTime.parse(timeStr.substring(0, 5));
            }
			return LocalTime.parse(timeStr, TIME_FORMATTER);
		} catch (Exception e) {
			return null; // Handle parse error safely
		}
	}
    
    private String formatTime(LocalTime time) {
        if (time == null) return null;
        return time.format(TIME_FORMATTER);
    }
	
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

      @GET
      @Path("/search")
      @Produces(MediaType.APPLICATION_JSON)
      public Response searchDentists(@QueryParam("keyword") String keyword, 
                                     @QueryParam("gouvernorat") String gouvernorat) {
          
          java.util.List<Dentiste> dentistes = userDAO.searchDentists(keyword, gouvernorat);
          java.util.List<DentisteSearchDTO> dtos = new java.util.ArrayList<>();
          
          for (Dentiste d : dentistes) {
              DentisteSearchDTO dto = new DentisteSearchDTO(
                  d.getId(), d.getNom(), d.getPrenom(), d.getGouvernorat(), 
                  d.getDelegation(), d.getAdresse(), d.getDiplome(), d.getSpecialite()
              );
              dto.setPhoto(d.getPhoto());
              dtos.add(dto);
          }
          return Response.ok(dtos).build();
      }

      @GET
      @Path("/dentist/{id}")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getDentistDetails(@PathParam("id") int id) {
          User user = userDAO.findById(id);
          if (user == null || !(user instanceof Dentiste)) {
              return Response.status(Response.Status.NOT_FOUND).entity("Dentist not found").build();
          }
          
          Dentiste d = (Dentiste) user;
          
          // Use Map to avoid compilation issues with new DTO class
          java.util.Map<String, Object> responseMap = new java.util.HashMap<>();
          
          // Basic Dentist Info
          responseMap.put("id", d.getId());
          responseMap.put("nom", d.getNom());
          responseMap.put("prenom", d.getPrenom());
          responseMap.put("gouvernorat", d.getGouvernorat());
          responseMap.put("delegation", d.getDelegation());
          responseMap.put("adresse", d.getAdresse());
          responseMap.put("diplome", d.getDiplome());
          responseMap.put("specialite", d.getSpecialite());
          responseMap.put("tel", d.getTel());
          responseMap.put("photo", d.getPhoto());
          
          // Fetch Services
          java.util.List<ServiceMedical> services = userDAO.getDentistServices(id);
          java.util.List<ServiceMedicalDTO> serviceDTOs = new java.util.ArrayList<>();
          for (ServiceMedical s : services) {
              ServiceMedicalDTO sDto = new ServiceMedicalDTO();
              sDto.setNumSM(s.getNumSM());
              sDto.setNomSM(s.getNomSM());
              sDto.setTypeSM(s.getTypeSM() != null ? s.getTypeSM().toString() : null);
              sDto.setDescriptionSM(s.getDescriptionSM());
              sDto.setTarifSM(s.getTarifSM());
              sDto.setDentistId(d.getId());
              serviceDTOs.add(sDto);
          }
          responseMap.put("services", serviceDTOs);
          
          // Fetch Available Rendezvous
          java.util.List<Rendezvous> rendezvousList = userDAO.getAvailableRendezvous(id);
          java.util.List<RendezvousDTO> rvDTOs = new java.util.ArrayList<>();
          for (Rendezvous r : rendezvousList) {
              RendezvousDTO rvDto = new RendezvousDTO();
              rvDto.setIdRv(r.getIdRv());
              rvDto.setDentistId(d.getId());
              rvDto.setDateRv(r.getDateRv());
              rvDto.setHeureRv(r.getHeureRv());
              rvDto.setStatutRv(r.getStatutRv().toString());
              rvDto.setDescriptionRv(r.getDescriptionRv());
              rvDto.setDentistName(d.getNom() + " " + d.getPrenom());
              
              rvDTOs.add(rvDto);
          }
          responseMap.put("availableRendezvous", rvDTOs);
          
          return Response.ok(responseMap).build();
      }

      @PUT
      @Path("/dentist/{id}")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Response updateDentistProfile(@PathParam("id") int id, DentisteSearchDTO dto) {
          User user = userDAO.findById(id);
          if (user == null || !(user instanceof Dentiste)) {
              return Response.status(Response.Status.NOT_FOUND).entity("Dentist not found").build();
          }

          Dentiste d = (Dentiste) user;
          // Update allowed fields
          if (dto.getNom() != null) d.setNom(dto.getNom());
          if (dto.getPrenom() != null) d.setPrenom(dto.getPrenom());
          if (dto.getGouvernorat() != null) d.setGouvernorat(dto.getGouvernorat());
          if (dto.getDelegation() != null) d.setDelegation(dto.getDelegation());
          if (dto.getAdresse() != null) d.setAdresse(dto.getAdresse());
          if (dto.getDiplome() != null) d.setDiplome(dto.getDiplome());
          if (dto.getSpecialite() != null) d.setSpecialite(dto.getSpecialite());
          if (dto.getPhoto() != null) d.setPhoto(dto.getPhoto());
          if (dto.getTel() != null) d.setTel(dto.getTel());

          userDAO.updateDentist(d);

          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "Profile updated successfully");
          }}).build();
      }

      @GET
      @Path("/dentist/{id}/horaires")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getDentistHoraires(@PathParam("id") int id) {
          java.util.List<Horaire> horaires = userDAO.getDentistHoraires(id);
          java.util.List<HoraireDTO> dtos = new java.util.ArrayList<>();
          
          for (Horaire h : horaires) {
              HoraireDTO dto = new HoraireDTO();
              dto.setJourSemaine(h.getJourSemaine().toString());
              dto.setMatinDebut(formatTime(h.getMatinDebut()));
              dto.setMatinFin(formatTime(h.getMatinFin()));
              dto.setApresMidiDebut(formatTime(h.getApresMidiDebut()));
              dto.setApresMidiFin(formatTime(h.getApresMidiFin()));
              dto.setEstFerme(h.isEstFerme());
              dtos.add(dto);
          }
           return Response.ok(dtos).build();
      }

      @PUT
      @Path("/dentist/{id}/horaires")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Response updateDentistHoraires(@PathParam("id") int id, java.util.List<HoraireDTO> dtos) {
          User user = userDAO.findById(id);
          if (user == null || !(user instanceof Dentiste)) {
              return Response.status(Response.Status.NOT_FOUND).entity("Dentist not found").build();
          }

          java.util.List<Horaire> horaires = new java.util.ArrayList<>();
          for (HoraireDTO dto : dtos) {
              Horaire h = new Horaire();
              h.setJourSemaine(JourSemaine.valueOf(dto.getJourSemaine()));
              
              h.setMatinDebut(parseTime(dto.getMatinDebut()));
              h.setMatinFin(parseTime(dto.getMatinFin()));
              h.setApresMidiDebut(parseTime(dto.getApresMidiDebut()));
              h.setApresMidiFin(parseTime(dto.getApresMidiFin()));
              h.setEstFerme(dto.isEstFerme());

              horaires.add(h);
          }

          userDAO.updateDentistHoraires(id, horaires);

          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "Horaires updated successfully");
          }}).build();
      }

      // ============== ADMIN ENDPOINTS ==============

      private User getAuthenticatedUser(HttpServletRequest req) {
          String authHeader = req.getHeader("Authorization");
          if (authHeader != null && authHeader.startsWith("Bearer ")) {
              String token = authHeader.substring(7);
              try {
                  Key key = Keys.hmacShaKeyFor("MaCleSuperSecrete1234567890123456".getBytes(StandardCharsets.UTF_8));
                  io.jsonwebtoken.Claims claims = Jwts.parserBuilder()
                          .setSigningKey(key)
                          .build()
                          .parseClaimsJws(token)
                          .getBody();
                  String email = claims.getSubject();
                  if (email != null) {
                      return userDAO.findByEmail(email);
                  }
              } catch (Exception e) {
                  System.out.println("JWT Warning: " + e.getMessage());
              }
          }
          return null;
      }

      @GET
      @Path("/users")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getAllUsers(@Context HttpServletRequest req) {
          User currentUser = getAuthenticatedUser(req);
          if (currentUser == null || !(currentUser instanceof Admin)) {
              return Response.status(Response.Status.FORBIDDEN)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "Only admin can access this resource");
                  }}).build();
          }
          
          java.util.List<User> users = userDAO.findAll();
          
          java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
          for (User u : users) {
              java.util.Map<String, Object> userMap = new java.util.HashMap<>();
              userMap.put("id", u.getId());
              userMap.put("nom", u.getNom());
              userMap.put("prenom", u.getPrenom());
              userMap.put("email", u.getEmail());
              userMap.put("tel", u.getTel());
              userMap.put("photo", u.getPhoto());
              
              if (u instanceof Dentiste) {
                  Dentiste d = (Dentiste) u;
                  userMap.put("role", "DENTISTE");
                  userMap.put("gouvernorat", d.getGouvernorat());
                  userMap.put("delegation", d.getDelegation());
                  userMap.put("adresse", d.getAdresse());
                  userMap.put("specialite", d.getSpecialite());
                  userMap.put("diplome", d.getDiplome());
              } else if (u instanceof Patient) {
                  userMap.put("role", "PATIENT");
                  userMap.put("dateNaissance", ((Patient) u).getDateNaissanceP());
                  userMap.put("groupeSanguin", ((Patient) u).getGroupeSanguinP());
                  userMap.put("recouvrement", ((Patient) u).getRecouvrementP());
              } else if (u instanceof Admin) {
                  userMap.put("role", "ADMIN");
              }
              
              result.add(userMap);
          }
          
          return Response.ok(result).build();
      }

      @GET
      @Path("/dentists")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getAllDentists() {
          java.util.List<User> users = userDAO.findAll();
          
          java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
          for (User u : users) {
              if (u instanceof Dentiste) {
                  Dentiste d = (Dentiste) u;
                  java.util.Map<String, Object> dentistMap = new java.util.HashMap<>();
                  dentistMap.put("id", d.getId());
                  dentistMap.put("nom", d.getNom());
                  dentistMap.put("prenom", d.getPrenom());
                  dentistMap.put("email", d.getEmail());
                  dentistMap.put("tel", d.getTel());
                  dentistMap.put("gouvernorat", d.getGouvernorat());
                  dentistMap.put("delegation", d.getDelegation());
                  dentistMap.put("adresse", d.getAdresse());
                  dentistMap.put("photo", d.getPhoto());
                  dentistMap.put("specialite", d.getSpecialite());
                  dentistMap.put("diplome", d.getDiplome());
                  result.add(dentistMap);
              }
          }
          
          return Response.ok(result).build();
      }

      @GET
      @Path("/patients")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getAllPatients(@Context HttpServletRequest req) {
          User currentUser = getAuthenticatedUser(req);
          if (currentUser == null || !(currentUser instanceof Admin)) {
              return Response.status(Response.Status.FORBIDDEN)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "Only admin can access this resource");
                  }}).build();
          }
          
          java.util.List<User> users = userDAO.findAll();
          
          java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();
          for (User u : users) {
              if (u instanceof Patient) {
                  Patient p = (Patient) u;
                  java.util.Map<String, Object> patientMap = new java.util.HashMap<>();
                  patientMap.put("id", p.getId());
                  patientMap.put("nom", p.getNom());
                  patientMap.put("prenom", p.getPrenom());
                  patientMap.put("email", p.getEmail());
                  patientMap.put("tel", p.getTel());
                  patientMap.put("gouvernorat", p.getGouvernorat());
                  patientMap.put("delegation", p.getDelegation());
                  patientMap.put("adresse", p.getAdresse());
                  patientMap.put("photo", p.getPhoto());
                  patientMap.put("dateNaissance", p.getDateNaissanceP());
                  patientMap.put("groupeSanguin", p.getGroupeSanguinP());
                  patientMap.put("recouvrement", p.getRecouvrementP());
                  result.add(patientMap);
              }
          }
          
          return Response.ok(result).build();
      }

      @GET
      @Path("/user/{id}")
      @Produces(MediaType.APPLICATION_JSON)
      public Response getUserById(@PathParam("id") int id) {
          User user = userDAO.findById(id);
          
          if (user == null) {
              return Response.status(Response.Status.NOT_FOUND)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "User not found");
                  }}).build();
          }
          
          java.util.Map<String, Object> userMap = new java.util.HashMap<>();
          userMap.put("id", user.getId());
          userMap.put("nom", user.getNom());
          userMap.put("prenom", user.getPrenom());
          userMap.put("email", user.getEmail());
          userMap.put("tel", user.getTel());
          userMap.put("sexe", user.getSexe() != null ? user.getSexe().toString() : null);
          userMap.put("photo", user.getPhoto());
          
          if (user instanceof Dentiste) {
              Dentiste d = (Dentiste) user;
              userMap.put("role", "DENTISTE");
              userMap.put("gouvernorat", d.getGouvernorat());
              userMap.put("delegation", d.getDelegation());
              userMap.put("adresse", d.getAdresse());
              userMap.put("specialite", d.getSpecialite());
              userMap.put("diplome", d.getDiplome());
          } else if (user instanceof Patient) {
              Patient p = (Patient) user;
              userMap.put("role", "PATIENT");
              userMap.put("dateNaissance", p.getDateNaissanceP() != null ? p.getDateNaissanceP().toString() : null);
              userMap.put("groupeSanguin", p.getGroupeSanguinP() != null ? p.getGroupeSanguinP().toString() : null);
              userMap.put("recouvrement", p.getRecouvrementP() != null ? p.getRecouvrementP().toString() : null);
          } else if (user instanceof Admin) {
              userMap.put("role", "ADMIN");
          }
          
          return Response.ok(userMap).build();
      }

      @PUT
      @Path("/user/{id}")
      @Consumes(MediaType.APPLICATION_JSON)
      @Produces(MediaType.APPLICATION_JSON)
      public Response updateUser(@PathParam("id") int id, java.util.Map<String, Object> data, @Context HttpServletRequest req) {
          User currentUser = getAuthenticatedUser(req);
          
          if (currentUser == null) {
              return Response.status(Response.Status.UNAUTHORIZED)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "Unauthorized");
                  }}).build();
          }
          
          boolean isAdmin = currentUser instanceof Admin;
          boolean isSelf = currentUser.getId() == id;
          
          if (!isAdmin && !isSelf) {
              return Response.status(Response.Status.FORBIDDEN)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "You can only update your own profile");
                  }}).build();
          }
          
          User user = userDAO.findById(id);
          if (user == null) {
              return Response.status(Response.Status.NOT_FOUND)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "User not found");
                  }}).build();
          }
          
          // Update common User fields
          if (data.containsKey("nom") && data.get("nom") != null) {
              user.setNom((String) data.get("nom"));
          }
          if (data.containsKey("prenom") && data.get("prenom") != null) {
              user.setPrenom((String) data.get("prenom"));
          }
          if (data.containsKey("email") && data.get("email") != null) {
              user.setEmail((String) data.get("email"));
          }
          if (data.containsKey("tel") && data.get("tel") != null) {
              user.setTel(Integer.parseInt(data.get("tel").toString()));
          }
          if (data.containsKey("sexe") && data.get("sexe") != null) {
              user.setSexe(com.enit.backoffice.entity.Sexe.valueOf((String) data.get("sexe")));
          }
          if (data.containsKey("photo") && data.get("photo") != null) {
              user.setPhoto((String) data.get("photo"));
          }
          
          // Update Dentiste-specific fields
          if (user instanceof Dentiste) {
              Dentiste d = (Dentiste) user;
              if (data.containsKey("gouvernorat") && data.get("gouvernorat") != null) {
                  d.setGouvernorat((String) data.get("gouvernorat"));
              }
              if (data.containsKey("delegation") && data.get("delegation") != null) {
                  d.setDelegation((String) data.get("delegation"));
              }
              if (data.containsKey("adresse") && data.get("adresse") != null) {
                  d.setAdresse((String) data.get("adresse"));
              }
              if (data.containsKey("specialite") && data.get("specialite") != null) {
                  d.setSpecialite((String) data.get("specialite"));
              }
              if (data.containsKey("diplome") && data.get("diplome") != null) {
                  d.setDiplome((String) data.get("diplome"));
              }
          }
          
          // Update Patient-specific fields
          if (user instanceof Patient) {
              Patient p = (Patient) user;
              if (data.containsKey("dateNaissance") && data.get("dateNaissance") != null) {
                  p.setDateNaissanceP(java.time.LocalDate.parse((String) data.get("dateNaissance")));
              }
              if (data.containsKey("groupeSanguin") && data.get("groupeSanguin") != null) {
                  p.setGroupeSanguinP(com.enit.backoffice.entity.GroupeSanguin.valueOf((String) data.get("groupeSanguin")));
              }
              if (data.containsKey("recouvrement") && data.get("recouvrement") != null) {
                  p.setRecouvrementP(com.enit.backoffice.entity.TypeRecouvrement.valueOf((String) data.get("recouvrement")));
              }
          }
          
          userDAO.updateUser(user);
          
          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "User updated successfully");
              put("id", user.getId());
          }}).build();
      }

      @jakarta.ws.rs.DELETE
      @Path("/user/{id}")
      @Produces(MediaType.APPLICATION_JSON)
      public Response deleteUser(@PathParam("id") int id, @Context HttpServletRequest req) {
          User currentUser = getAuthenticatedUser(req);
          
          if (currentUser == null || !(currentUser instanceof Admin)) {
              return Response.status(Response.Status.FORBIDDEN)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "Only admin can delete users");
                  }}).build();
          }
          
          User user = userDAO.findById(id);
          if (user == null) {
              return Response.status(Response.Status.NOT_FOUND)
                  .entity(new java.util.HashMap<String, Object>() {{
                      put("error", "User not found");
                  }}).build();
          }
          
          userDAO.deleteUser(id);
          
          final int deletedId = id;
          return Response.ok(new java.util.HashMap<String, Object>() {{
              put("message", "User deleted successfully");
              put("id", deletedId);
          }}).build();
      }
}