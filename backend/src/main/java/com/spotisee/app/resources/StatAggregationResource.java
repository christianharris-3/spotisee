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

import static com.spotisee.app.config.Constants.TIMESTAMP_LOWER_BOUND;
import static com.spotisee.app.config.Constants.TIMESTAMP_UPPER_BOUND;
import static com.spotisee.app.config.Constants.PAGE_SIZE;

@Path("api/aggregate/")
@Produces(MediaType.APPLICATION_JSON)
public class StatAggregationResource {

    private static final Logger log = LoggerFactory.getLogger(StatAggregationResource.class);


    private final MusicDataManager musicDataManager;

    public StatAggregationResource(SongDataDao songDataDao) {
        this.musicDataManager = new MusicDataManager(songDataDao);
    }

    @GET
    @Path("songs")
    public Response collectSongStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
    ) {
        List<SongStats> songStats = musicDataManager.collectSongStats(uploadId, startDate, endDate, pageSize, pageIndex);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("albums")
    public Response collectAlbumStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
    ) {
        List<AlbumStats> songStats = musicDataManager.collectAlbumStats(uploadId, startDate, endDate, pageSize, pageIndex);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("artists")
    public Response collectArtistStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
    ) {
        List<ArtistStats> songStats = musicDataManager.collectArtistStats(uploadId, startDate, endDate, pageSize, pageIndex);
        return Response.ok(songStats).build();
    }

    @GET
    @Path("all")
    public Response collectAllStats(
            @QueryParam("uploadId") long uploadId,
            @DefaultValue(TIMESTAMP_LOWER_BOUND) @QueryParam("start") String startDate,
            @DefaultValue(TIMESTAMP_UPPER_BOUND) @QueryParam("end") String endDate,
            @DefaultValue(PAGE_SIZE) @QueryParam("pageSize") int pageSize,
            @DefaultValue("0") @QueryParam("pageIndex") int pageIndex
    ) {
        List<CombinedStats> songStats = musicDataManager.collectAllStats(uploadId, startDate, endDate, pageSize, pageIndex);
        return Response.ok(songStats).build();
    }
}
