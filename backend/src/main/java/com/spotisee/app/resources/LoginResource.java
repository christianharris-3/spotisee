package com.spotisee.app.resources;

import com.spotisee.app.config.SpotiseeAuthenticator;
import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.models.User;
import com.spotisee.app.models.requests.LoginRequest;
import com.spotisee.app.models.response.ErrorResponse;
import com.spotisee.app.models.response.TokenResponse;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("api/auth/")
@Produces(MediaType.APPLICATION_JSON)
public class LoginResource {

    private final SpotiseeAuthenticator spotiseeAuthenticator;

    public LoginResource(SpotiseeAuthenticator spotiseeAuthenticator) {
        this.spotiseeAuthenticator = spotiseeAuthenticator;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("login")
    public Response login(@Valid @NotNull LoginRequest request) {
        Optional<String> token = spotiseeAuthenticator.generateToken(
                request.getUsername(),
                request.getPassword()
        );
        if (token.isPresent()) {
            return Response.ok(new TokenResponse(token.get())).build();
        }
        return Response.status(401).entity(new ErrorResponse(401, "invalid username or password")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("register")
    public Response register(@Valid @NotNull LoginRequest request) {
        if (!spotiseeAuthenticator.validUsername(request.getUsername())) {
            return Response.status(406, "Username Already Used").build();
        }
        spotiseeAuthenticator.register(request.getUsername(), request.getPassword());
        return Response.accepted().build();
    }
}