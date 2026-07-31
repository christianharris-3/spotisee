package com.spotisee.app.dao;

import com.spotisee.app.models.dao.UploadedSong;
import com.spotisee.app.resources.UploadDataResource;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface DataPreProcessingDao {
    @RegisterBeanMapper(UploadedSong.class)
    @SqlQuery("""
        SELECT timestamp, msPlayed, trackName, albumName, artistName, spotifyTrackUri
        FROM UploadItems
        WHERE (:uploadId = uploadId) AND
        (timestamp IS NOT NULL) AND
        (msPlayed IS NOT NULL) AND
        (trackName IS NOT NULL) AND
        (albumName IS NOT NULL) AND
        (artistName IS NOT NULL) AND
        (spotifyTrackUri IS NOT NULL);
    """)
    List<UploadedSong> loadSongUploads(@Bind("uploadId") long uploadId);
}
