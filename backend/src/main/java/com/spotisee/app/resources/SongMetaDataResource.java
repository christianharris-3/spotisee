package com.spotisee.app.resources;

import com.spotisee.app.managers.SongMetaDataManager;
import com.spotisee.app.managers.UserValidationManager;
import com.spotisee.app.models.User;
import com.spotisee.app.models.enums.ItemType;
import com.spotisee.app.models.response.YearWithMonths;
import io.dropwizard.auth.Auth;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("/api/aggregation-info")
@Produces(MediaType.APPLICATION_JSON)
public class SongMetaDataResource {

    private final SongMetaDataManager songMetaDataManager;
    private final UserValidationManager userValidationManager;

    public SongMetaDataResource(SongMetaDataManager songMetaDataManager, UserValidationManager userValidationManager) {
        this.songMetaDataManager = songMetaDataManager;
        this.userValidationManager = userValidationManager;
    }

    @GET
    @Path("/{uploadId}")
    public Response getDatesAvailable(@Auth User user,
                                      @PathParam("uploadId") long uploadId,
                                      @QueryParam("searchTerm") String searchTerm,
                                      @QueryParam("itemType") String itemType) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        List<YearWithMonths> yearWithMonths = songMetaDataManager.getYearsAvailable(uploadId, searchTerm, itemType);
        return Response.ok(yearWithMonths).build();
    }
}
