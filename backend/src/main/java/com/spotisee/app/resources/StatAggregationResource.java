package com.spotisee.app.resources;

import com.spotisee.app.dao.SongDataDao;
import com.spotisee.app.managers.MusicDataManager;
import com.spotisee.app.models.dao.AlbumStats;
import com.spotisee.app.models.dao.ArtistStats;
import com.spotisee.app.models.dao.CombinedStats;
import com.spotisee.app.models.dao.SongStats;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Path("api/aggregate/")
@Produces(MediaType.APPLICATION_JSON)
public class StatAggregationResource {

    private static final Logger log = LoggerFactory.getLogger(StatAggregationResource.class);

    private static final String TIMESTAMP_LOWER_BOUND = "1970-01-01 00:00:00";
    private static final String TIMESTAMP_UPPER_BOUND = "2040-01-01 00:00:00";


    private final MusicDataManager musicDataManager;

    public StatAggregationResource(SongDataDao songDataDao) {
        this.musicDataManager = new MusicDataManager(songDataDao);
    }

    @GET
    @Path("songs")
    public Response collectSongStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate
    ) {
        List<SongStats> songStats = musicDataManager.collectSongStats(uploadId, startDate, endDate);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("albums")
    public Response collectAlbumStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate
    ) {
        List<AlbumStats> songStats = musicDataManager.collectAlbumStats(uploadId, startDate, endDate);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("artists")
    public Response collectArtistStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate
    ) {
        List<ArtistStats> songStats = musicDataManager.collectArtistStats(uploadId, startDate, endDate);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("all")
    public Response collectAllStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate
    ) {
        List<CombinedStats> songStats = musicDataManager.collectAllStats(uploadId, startDate, endDate);
        return Response.ok(songStats).build();
    }
}
