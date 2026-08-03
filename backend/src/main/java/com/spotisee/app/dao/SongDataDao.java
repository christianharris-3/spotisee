package com.spotisee.app.dao;

import com.spotisee.app.models.dao.*;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
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
                SELECT uploadId, trackName,
                SUM(msPlayed) AS totalMsPlayed,
                albumName,
                artistName,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens,
                SUM(skipped) as skips
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end) AND
                (trackName LIKE :searchTerm OR albumName LIKE :searchTerm OR artistName LIKE :searchTerm)
                GROUP BY uploadId, trackName, albumName, artistName
                ORDER BY <sortPrimary> DESC, <sortSecondary> DESC
                LIMIT :pageSize OFFSET :pageOffset;
            """)
    List<SongStats> collectSongStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end,
            @Bind("searchTerm") String searchTerm,
            @Bind("pageSize") int pageSize,
            @Bind("pageOffset") int pageOffset,
            @Define("sortPrimary") String sortPrimary,
            @Define("sortSecondary") String sortSecondary
    );

    @RegisterBeanMapper(AlbumStats.class)
    @SqlQuery("""
                SELECT uploadId, albumName,
                SUM(msPlayed) AS totalMsPlayed,
                artistName,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens,
                SUM(skipped) as skips
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end) AND
                (albumName LIKE :searchTerm OR artistName LIKE :searchTerm)
                GROUP BY uploadId, albumName, artistName
                ORDER BY <sortPrimary> DESC, <sortSecondary> DESC
                LIMIT :pageSize OFFSET :pageOffset;
            """)
    List<AlbumStats> collectAlbumStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end,
            @Bind("searchTerm") String searchTerm,
            @Bind("pageSize") int pageSize,
            @Bind("pageOffset") int pageOffset,
            @Define("sortPrimary") String sortPrimary,
            @Define("sortSecondary") String sortSecondary
    );

    @RegisterBeanMapper(ArtistStats.class)
    @SqlQuery("""
                SELECT uploadId, artistName,
                SUM(msPlayed) AS totalMsPlayed,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens,
                SUM(skipped) as skips
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end) AND
                (artistName LIKE :searchTerm)
                GROUP BY uploadId, artistName
                ORDER BY <sortPrimary> DESC, <sortSecondary> DESC
                LIMIT :pageSize OFFSET :pageOffset;
            """)
    List<ArtistStats> collectArtistStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end,
            @Bind("searchTerm") String searchTerm,
            @Bind("pageSize") int pageSize,
            @Bind("pageOffset") int pageOffset,
            @Define("sortPrimary") String sortPrimary,
            @Define("sortSecondary") String sortSecondary
    );

    @RegisterBeanMapper(CombinedStats.class)
    @SqlQuery("""
                SELECT uploadId,
                SUM(msPlayed) AS totalMsPlayed,
                COUNT(*) AS count,
                SUM(msPlayed >=  30000) AS listens,
                SUM(skipped) as skips
                FROM SongView
                WHERE (:uploadId = uploadId) AND
                (:start < endTime) AND (endTime < :end)
                GROUP BY uploadId
                ORDER BY <sortPrimary> DESC, <sortSecondary> DESC
                LIMIT :pageSize OFFSET :pageOffset;
            """)
    List<CombinedStats> collectAllStats(
            @Bind("uploadId") long uploadId,
            @Bind("start") Timestamp start,
            @Bind("end") Timestamp end,
            @Bind("pageSize") int pageSize,
            @Bind("pageOffset") int pageOffset,
            @Define("sortPrimary") String sortPrimary,
            @Define("sortSecondary") String sortSecondary
    );


}
