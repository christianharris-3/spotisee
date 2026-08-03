package com.spotisee.app.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/selection/")
@Produces(MediaType.APPLICATION_JSON)
public class GraphSelectionResource {

    @GET()
    public Response getSelections(@) {

    }
}