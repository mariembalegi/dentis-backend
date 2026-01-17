package com.enit.backoffice.controllers;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@ApplicationPath("/api") 
public class DentisRestApp extends Application {
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON) 
	public Response helloWorld() {
		return Response.status(Response.Status.OK).build();
	}
}
