package com.spotisee.app.resources;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.dao.SongDataDao;
import com.spotisee.app.managers.MusicDataManager;
import com.spotisee.app.managers.UserValidationManager;
import com.spotisee.app.models.User;
import com.spotisee.app.models.dao.AlbumStats;
import com.spotisee.app.models.dao.ArtistStats;
import com.spotisee.app.models.dao.CombinedStats;
import com.spotisee.app.models.dao.SongStats;
import io.dropwizard.auth.Auth;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.spotisee.app.config.Constants.TIMESTAMP_LOWER_BOUND;
import static com.spotisee.app.config.Constants.TIMESTAMP_UPPER_BOUND;
import static com.spotisee.app.config.Constants.PAGE_SIZE;
import static com.spotisee.app.config.Constants.DEFAULT_SORT;

@Path("api/aggregate/")
@Produces(MediaType.APPLICATION_JSON)
public class StatAggregationResource {

    private static final Logger log = LoggerFactory.getLogger(StatAggregationResource.class);

    private final MusicDataManager musicDataManager;
    private final UserValidationManager userValidationManager;

    public StatAggregationResource(MusicDataManager musicDataManager, UserValidationManager userValidationManager) {
        this.musicDataManager = musicDataManager;
        this.userValidationManager = userValidationManager;
    }

    @GET
    @Path("songs/{uploadId}")
    public Response collectSongStats(
            @Auth User user,
            @PathParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue("") @QueryParam("searchTerm") String searchTerm,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex,
            @DefaultValue(DEFAULT_SORT) @QueryParam("sortBy") String sortBy
    ) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        List<SongStats> songStats = musicDataManager.collectSongStats(uploadId, startDate, endDate, searchTerm, pageSize, pageIndex, sortBy);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("albums/{uploadId}")
    public Response collectAlbumStats(
            @Auth User user,
            @PathParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue("") @QueryParam("searchTerm") String searchTerm,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex,
            @DefaultValue(DEFAULT_SORT) @QueryParam("sortBy") String sortBy
    ) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        List<AlbumStats> songStats = musicDataManager.collectAlbumStats(uploadId, startDate, endDate, searchTerm, pageSize, pageIndex, sortBy);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("artists/{uploadId}")
    public Response collectArtistStats(
            @Auth User user,
            @PathParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue("") @QueryParam("searchTerm") String searchTerm,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex,
            @DefaultValue(DEFAULT_SORT) @QueryParam("sortBy") String sortBy
    ) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        List<ArtistStats> songStats = musicDataManager.collectArtistStats(uploadId, startDate, endDate, searchTerm, pageSize, pageIndex, sortBy);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("all/{uploadId}")
    public Response collectAllStats(
            @Auth User user,
            @PathParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex,
            @DefaultValue(DEFAULT_SORT) @QueryParam("sortBy") String sortBy
    ) {
        userValidationManager.validateUserHasUpload(user, uploadId);
        List<CombinedStats> songStats = musicDataManager.collectAllStats(uploadId, startDate, endDate, pageSize, pageIndex, sortBy);
        return Response.ok(songStats).build();
    }
}
