package com.spotisee.app.exceptions.mapper;

import io.dropwizard.auth.UnauthorizedHandler;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.util.Map;

public class UnauthorisedExceptionMapper implements UnauthorizedHandler {

    @Override
    public Response buildResponse(String prefix, String realm) {
        return Response.status(401).entity(Map.of("error", "Unauthoriseedddd")).build();
    }
}
