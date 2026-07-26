package com.spotisee.app.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("api/login")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    @GET
    public String hello() {
        return "{\"message\":\"Hello World\"}";
    }
}