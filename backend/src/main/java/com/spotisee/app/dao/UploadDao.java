package com.spotisee.app.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;

public interface UploadDao {

    @SqlUpdate("""
        INSERT INTO Upload (userId)
        VALUES (:userId);
    """)
    @GetGeneratedKeys
    long createUpload(@Bind("userId") long userId);

    @SqlUpdate("""
        INSERT INTO UploadItem (
            uploadId,
            timestamp,
            platform,
            msPlayed,
            country,
            ipAddress,
            trackName,
            albumName,
            artistName,
            spotifyTrackUri,
            episodeName,
            episodeShowName,
            spotifyEpisodeUri,
            audiobookUri,
            audiobookChapterUri,
            audiobookChapterTitle,
            reasonEnd,
            reasonStart,
            shuffle,
            skipped,
            offline,
            offlineTimestamp,
            incognitoMode
        ) VALUES (
            :uploadId,
            :timestamp,
            :platform,
            :msPlayed,
            :country,
            :ipAddress,
            :trackName,
            :albumName,
            :artistName,
            :spotifyTrackUri,
            :episodeName,
            :episodeShowName,
            :spotifyEpisodeUri,
            :audiobookUri,
            :audiobookChapterUri,
            :audiobookChapterTitle,
            :reasonEnd,
            :reasonStart,
            :shuffle,
            :skipped,
            :offline,
            :offlineTimestamp,
            :incognitoMode
        );
    """)
    void createUploadItem(
            @Bind("uploadId") long uploadId,
            @Bind("timestamp") LocalDateTime timestamp,
            @Bind("platform") String platform,
            @Bind("msPlayed") int msPlayed,
            @Bind("country") String country,
            @Bind("ipAddress") String ipAddress,
            @Bind("trackName") String trackName,
            @Bind("albumName") String albumName,
            @Bind("artistName") String artistName,
            @Bind("spotifyTrackUri") String spotifyTrackUri,
            @Bind("episodeName") String episodeName,
            @Bind("episodeShowName") String episodeShowName,
            @Bind("spotifyEpisodeUri") String spotifyEpisodeUri,
            @Bind("audiobookTitle") String audiobookTitle,
            @Bind("audiobookUri") String audiobookUri,
            @Bind("audiobookChapterUri") String audiobookChapterUri,
            @Bind("audiobookChapterTitle") String audiobookChapterTitle,
            @Bind("reasonEnd") String reasonEnd,
            @Bind("reasonStart") String reasonStart,
            @Bind("shuffle") Boolean shuffle,
            @Bind("skipped") Boolean skipped,
            @Bind("offline") Boolean offline,
            @Bind("offlineTimestamp") LocalDateTime offlineTimestamp,
            @Bind("incognitoMode") Boolean incognitoMode
    );
}
