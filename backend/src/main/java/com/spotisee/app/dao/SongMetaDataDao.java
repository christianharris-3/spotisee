package com.spotisee.app.dao;

import com.spotisee.app.models.dao.MonthYearPair;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface SongMetaDataDao {

    @RegisterBeanMapper(MonthYearPair.class)
    @SqlQuery("""
            SELECT YEAR(timestamp) as year,
                   MONTH(timestamp) as month
            FROM UploadItem
            WHERE (uploadId = :uploadId) AND
            (trackName LIKE :searchTerm OR albumName LIKE :searchTerm OR artistName LIKE :searchTerm)
            GROUP BY year, month;
            """)
    List<MonthYearPair> getYearsAvailableSong(@Bind("uploadId") long uploadId, @Bind("searchTerm") String searchTerm);

    @RegisterBeanMapper(MonthYearPair.class)
    @SqlQuery("""
            SELECT YEAR(timestamp) as year,
                   MONTH(timestamp) as month
            FROM UploadItem
            WHERE (uploadId = :uploadId) AND
            (albumName LIKE :searchTerm OR artistName LIKE :searchTerm)
            GROUP BY year, month;
            """)
    List<MonthYearPair> getYearsAvailableAlbum(@Bind("uploadId") long uploadId, @Bind("searchTerm") String searchTerm);

    @RegisterBeanMapper(MonthYearPair.class)
    @SqlQuery("""
            SELECT YEAR(timestamp) as year,
                   MONTH(timestamp) as month
            FROM UploadItem
            WHERE (uploadId = :uploadId) AND
            (artistName LIKE :searchTerm)
            GROUP BY year, month;
            """)
    List<MonthYearPair> getYearsAvailableArtist(@Bind("uploadId") long uploadId, @Bind("searchTerm") String searchTerm);

    @RegisterBeanMapper(MonthYearPair.class)
    @SqlQuery("""
            SELECT YEAR(timestamp) as year,
                   MONTH(timestamp) as month
            FROM UploadItem
            WHERE (uploadId = :uploadId)
            GROUP BY year, month;
            """)
    List<MonthYearPair> getYearsAvailableCombined(@Bind("uploadId") long uploadId);


}
