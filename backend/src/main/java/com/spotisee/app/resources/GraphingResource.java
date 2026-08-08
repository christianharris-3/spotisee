package com.spotisee.app.resources;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.dao.SelectionDao;
import com.spotisee.app.managers.GraphingManager;
import com.spotisee.app.managers.UserValidationManager;
import com.spotisee.app.models.User;
import com.spotisee.app.models.response.GraphData;
import io.dropwizard.auth.Auth;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("api/graph/")
@Produces(MediaType.APPLICATION_JSON)
public class GraphingResource {

    private final GraphingManager graphingManager;
    private final UserValidationManager userValidationManager;

    public GraphingResource(GraphingManager graphingManager, UserValidationManager userValidationManager) {
        this.graphingManager = graphingManager;
        this.userValidationManager = userValidationManager;
    }

    @GET
    @Path("{selectionId}")
    public Response getGraphData(@Auth User user, @PathParam("selectionId") long selectionId) {
        userValidationManager.validateUserHasSelection(user, selectionId);
        GraphData graphData = graphingManager.getGraphingData(user.getUserId(), selectionId);
        return Response.ok().build();
    }
}
