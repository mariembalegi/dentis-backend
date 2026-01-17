package com.enit.backoffice.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        System.out.println("=== CORS FILTER REQUEST === Method: " + requestContext.getMethod() + 
                          ", Origin: " + requestContext.getHeaderString("Origin") +
                          ", Path: " + requestContext.getUriInfo().getPath());
        
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            System.out.println("*** Handling OPTIONS preflight - aborting with 200 OK");
            
            requestContext.abortWith(Response.ok()
                .header("Access-Control-Allow-Origin", "http://localhost:4200")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, cache-control")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Max-Age", "3600")
                .build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, 
                      ContainerResponseContext responseContext) throws IOException {
        
        System.out.println("=== CORS FILTER RESPONSE === Status: " + responseContext.getStatus());
        
        // Set CORS headers for actual requests
        responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", "http://localhost:4200");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, x-requested-with, cache-control");
        responseContext.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().putSingle("Access-Control-Max-Age", "3600");
        
        System.out.println("*** CORS headers added to response");
    }
}
