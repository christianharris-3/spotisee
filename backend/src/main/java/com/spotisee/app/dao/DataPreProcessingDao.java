package com.spotisee.app.dao;

import com.spotisee.app.models.dao.SongInfo;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

public interface DataPreProcessingDao {
    @RegisterBeanMapper(SongInfo.class)
    @SqlQuery("""
        SELECT timestamp, msPlayed, trackName, albumName, artistName, spotifyTrackUri
        FROM UploadItem
        WHERE (:uploadId = uploadId) AND
        (timestamp IS NOT NULL) AND
        (msPlayed IS NOT NULL) AND
        (trackName IS NOT NULL) AND
        (albumName IS NOT NULL) AND
        (artistName IS NOT NULL) AND
        (spotifyTrackUri IS NOT NULL);
    """)
    List<SongInfo> loadSongUploads(@Bind("uploadId") long uploadId);
}
