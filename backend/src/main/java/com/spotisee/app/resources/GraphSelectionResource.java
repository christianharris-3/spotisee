package com.spotisee.app.resources;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.dao.SelectionDao;
import com.spotisee.app.managers.SelectionManager;
import com.spotisee.app.managers.UserValidationManager;
import com.spotisee.app.models.User;
import com.spotisee.app.models.dao.Selection;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.requests.SelectionItemRequest;
import com.spotisee.app.models.requests.SelectionRequest;
import com.spotisee.app.models.requests.UpdateSelectionItemRequest;
import io.dropwizard.auth.Auth;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("api/selection/")
@Produces(MediaType.APPLICATION_JSON)
public class GraphSelectionResource {

    private final SelectionManager selectionManager;
    private final UserValidationManager userValidationManager;

    public GraphSelectionResource(SelectionDao selectionDao, AuthDao authDao) {
        this.selectionManager = new SelectionManager(selectionDao);
        this.userValidationManager = new UserValidationManager(authDao);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSelection(@Auth User user, @Valid @NotNull SelectionRequest request) {
        long selectionId = selectionManager.createSelection(user.getUserId(), request.getSelectionTitle());
        return Response.ok(selectionId).build();
    }

    @GET
    public Response getSelections(@Auth User user) {
        List<Selection> selections = selectionManager.getSelections(user.getUserId());
        return Response.ok(selections).build();
    }

    @PUT
    @Path("{selectionId}")
    public Response updateSelection(
            @Auth User user,
            @PathParam("selectionId") long selectionId,
            @Valid @NotNull SelectionRequest request
    ) {
        userValidationManager.validateUserHasSelection(user, selectionId);
        selectionManager.updateSelection(user.getUserId(), selectionId, request.getSelectionTitle());
        return Response.accepted().build();
    }

    @DELETE
    @Path("{selectionId}")
    public Response deleteSelection(@Auth User user, @PathParam("selectionId") long selectionId) {
        userValidationManager.validateUserHasSelection(user, selectionId);
        selectionManager.deleteSelection(user.getUserId(), selectionId);
        return Response.accepted().build();
    }

    @POST
    @Path("{selectionId}")
    public Response createSelectionItem(
            @Auth User user,
            @PathParam("selectionId") long selectionId,
            @Valid @NotNull SelectionItemRequest request
    ) {
        userValidationManager.validateUserHasSelection(user, selectionId);
        long selectionItemId = selectionManager.createSelectionItem(
                selectionId,
                request.getTrackName(),
                request.getAlbumName(),
                request.getArtistName(),
                request.getItemType(),
                request.getGraphType(),
                request.getStartDate(),
                request.getEndDate()
        );
        return Response.ok(selectionItemId).build();
    }

    @GET
    @Path("{selectionId}")
    public Response getSelection(@Auth User user, @PathParam("selectionId") long selectionId) {
        userValidationManager.validateUserHasSelection(user, selectionId);
        SelectionResponse item = selectionManager.getSelectionItems(user.getUserId(), selectionId);
        return Response.ok(item).build();
    }

    @PUT
    @Path("{selectionId}/{selectionItemId}")
    public Response updateSelectionItem(
            @Auth User user,
            @PathParam("selectionId") long selectionId,
            @PathParam("selectionItemId") long selectionItemId,
            @NotNull @Valid UpdateSelectionItemRequest request
    ) {
        userValidationManager.validateUserHasSelectionItem(user, selectionId, selectionItemId);
        selectionManager.updateSelectionItem(
                selectionItemId,
                request.getGraphType(),
                request.getStartDate(),
                request.getEndDate()
        );
        return Response.accepted().build();
    }

    @DELETE
    @Path("{selectionId}/{selectionItemId}")
    public Response deleteSelectionItem(
            @Auth User user,
            @PathParam("selectionId") long selectionId,
            @PathParam("selectionItemId") long selectionItemId
    ) {
        userValidationManager.validateUserHasSelectionItem(user, selectionId, selectionItemId);
        selectionManager.deleteSelectionItem(selectionItemId);
        return Response.accepted().build();
    }
}