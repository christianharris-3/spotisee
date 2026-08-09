package com.spotisee.app.dao;

import com.spotisee.app.models.dao.UploadInfo;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.time.LocalDateTime;
import java.util.List;

public interface UploadDao {

    @RegisterBeanMapper(UploadInfo.class)
    @SqlQuery("""
            SELECT Upload.uploadId,
                   Upload.uploadName,
                   COUNT(UploadItem.uploadItemId) as itemCount,
                   MIN(UploadItem.timestamp) as startDate,
                   MAX(UploadItem.timestamp) as endDate
            FROM Upload LEFT JOIN UploadItem
            ON Upload.uploadid = UploadItem.uploadId
            WHERE (Upload.userId = :userId)
            GROUP BY Upload.uploadId
            ORDER BY Upload.uploadId;
            """)
    List<UploadInfo> getUploadInfo(@Bind("userId") long userId);

    @SqlUpdate("""
            UPDATE Upload
            SET uploadName = :uploadName
            WHERE :uploadId = uploadId;
            """)
    void updateUpload(@Bind("uploadId") long uploadId, @Bind("uploadName") String uploadName);


    @SqlUpdate("""
            DELETE FROM Upload
            WHERE :uploadId = uploadId;
            """)
    void deleteUpload(@Bind("uploadId") long uploadId);

    @SqlUpdate("""
            DELETE FROM UploadItem
            WHERE :uploadId = uploadId;
            """)
    void deleteUploadItems(@Bind("uploadId") long uploadId);

    @SqlUpdate("""
        INSERT INTO Upload (userId, uploadName)
        VALUES (:userId, :uploadName);
    """)
    @GetGeneratedKeys
    long createUpload(@Bind("userId") long userId, @Bind("uploadName") String uploadName);

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
