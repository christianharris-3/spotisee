package com.spotisee.app.dao;

import com.spotisee.app.models.dao.*;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.sql.Timestamp;
import java.util.List;

public interface SongDataDao {
    @RegisterBeanMapper(SongInfo.class)
    @SqlQuery("""
                SELECT * FROM SongView
                WHERE (:uploadId = uploadId);
            """)
    List<SongInfo> getAllSongs(@Bind("uploadId") long uploadId);

    @RegisterBeanMapper(SongInfo.class)
    @SqlQuery("""
                SELECT * FROM SongView
                WHERE (:uploadId = uploadId) AND (:start < endTime) AND (:end > endTime);
            """)
    List<SongInfo> getSongsBetweenDates(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end
    );

    @RegisterBeanMapper(SongStats.class)
    @SqlQuery("""
                SELECT MIN(uploadId), trackName,
                SUM(msPlayed) AS totalMsPlayed,
                MIN(albumName) AS albumName,
                MIN(artistName) AS artistName,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end)
                GROUP BY trackName;
            """)
    List<SongStats> collectSongStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end
    );

    @RegisterBeanMapper(AlbumStats.class)
    @SqlQuery("""
                SELECT MIN(uploadId), albumName,
                SUM(msPlayed) AS totalMsPlayed,
                MIN(artistName) AS artistName,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end)
                GROUP BY albumName;
            """)
    List<AlbumStats> collectAlbumStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end
    );

    @RegisterBeanMapper(ArtistStats.class)
    @SqlQuery("""
                SELECT MIN(uploadId), artistName,
                SUM(msPlayed) AS totalMsPlayed,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end)
                GROUP BY artistName;
            """)
    List<ArtistStats> collectArtistStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end
    );

    @RegisterBeanMapper(CombinedStats.class)
    @SqlQuery("""
                SELECT uploadId,
                SUM(msPlayed) AS totalMsPlayed,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end)
                GROUP BY uploadId;
            """)
    List<CombinedStats> collectAllStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end
    );


}
