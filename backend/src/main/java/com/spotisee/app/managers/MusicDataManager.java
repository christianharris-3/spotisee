package com.spotisee.app.managers;

import com.spotisee.app.dao.SongDataDao;
import com.spotisee.app.models.dao.AlbumStats;
import com.spotisee.app.models.dao.ArtistStats;
import com.spotisee.app.models.dao.CombinedStats;
import com.spotisee.app.models.dao.SongStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.List;

public class MusicDataManager {

    private static final Logger log = LoggerFactory.getLogger(MusicDataManager.class);

    private final SongDataDao songDataDao;

    public MusicDataManager(SongDataDao songDataDao) {
        this.songDataDao = songDataDao;
    }

    public List<SongStats> collectSongStats(long uploadId, String start, String end) {
        return this.songDataDao.collectSongStats(uploadId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<AlbumStats> collectAlbumStats(long uploadId, String start, String end) {
        return this.songDataDao.collectAlbumStats(uploadId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<ArtistStats> collectArtistStats(long uploadId, String start, String end) {
        return this.songDataDao.collectArtistStats(uploadId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }

    public List<CombinedStats> collectAllStats(long uploadId, String start, String end) {
        return this.songDataDao.collectAllStats(uploadId, Timestamp.valueOf(start), Timestamp.valueOf(end));
    }
}
