package com.spotisee.app.dao;

import com.spotisee.app.models.dao.SingleSong;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.Timestamp;
import java.util.List;

public interface GraphingDao {
    @SqlQuery("""
            SELECT endTime, msPlayed, trackName, albumName, artistName, (msPlayed > 30000) as listened
            FROM SongView
            WHERE (:start < endTime) AND (endTime < :end) AND
            (trackName = :trackName) AND (artistName = :artistName)
            ORDER BY endTime;
            """)
    List<SingleSong> getSongPoints(Timestamp start, Timestamp end, String trackName, String artistName);
}
