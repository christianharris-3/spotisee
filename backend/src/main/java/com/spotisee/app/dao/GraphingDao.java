package com.spotisee.app.dao;

import com.spotisee.app.models.dao.graph.*;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.Timestamp;
import java.util.List;

public interface GraphingDao {
    @RegisterBeanMapper(SingleSong.class)
    @SqlQuery("""
            SELECT endTime, msPlayed, trackName, albumName, artistName, (msPlayed > 30000) as listened
            FROM SongView
            WHERE (:start < endTime) AND (endTime < :end) AND
            (trackName = :trackName) AND (artistName = :artistName)
            ORDER BY endTime;
            """)
    List<SingleDataPoint> getSongPoints(Timestamp start, Timestamp end, String trackName, String albumName, String artistName);

    @RegisterBeanMapper(SingleAlbum.class)
    @SqlQuery("""
            SELECT endTime, msPlayed, trackName, albumName, artistName, (msPlayed > 30000) as listened
            FROM SongView
            WHERE (:start < endTime) AND (endTime < :end) AND
            (trackName = :trackName) AND (artistName = :artistName)
            ORDER BY endTime;
            """)
    List<SingleDataPoint> getAlbumPoints(Timestamp start, Timestamp end, String albumName, String artistName);

    @RegisterBeanMapper(SingleArtist.class)
    @SqlQuery("""
            SELECT endTime, msPlayed, trackName, albumName, artistName, (msPlayed > 30000) as listened
            FROM SongView
            WHERE (:start < endTime) AND (endTime < :end) AND
            (trackName = :trackName) AND (artistName = :artistName)
            ORDER BY endTime;
            """)
    List<SingleDataPoint> getArtistPoints(Timestamp start, Timestamp end, String artistName);

    @RegisterBeanMapper(SingleListen.class)
    @SqlQuery("""
            SELECT endTime, msPlayed, trackName, albumName, artistName, (msPlayed > 30000) as listened
            FROM SongView
            WHERE (:start < endTime) AND (endTime < :end) AND
            (trackName = :trackName) AND (artistName = :artistName)
            ORDER BY endTime;
            """)
    List<SingleDataPoint> getCombinedPoints(Timestamp start, Timestamp end);
}
