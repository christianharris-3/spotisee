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

import static com.spotisee.app.config.Constants.SORT_OPTIONS;

public class MusicDataManager {

    private static final Logger log = LoggerFactory.getLogger(MusicDataManager.class);

    private final SongDataDao songDataDao;

    public MusicDataManager(SongDataDao songDataDao) {
        this.songDataDao = songDataDao;
    }

    public List<SongStats> collectSongStats(
            long uploadId,
            String start,
            String end,
            String searchTerm,
            int pageSize,
            int pageIndex,
            String sortBy
    ) {
        List<String> sortByList = getSortBy(sortBy);
        return this.songDataDao.collectSongStats(
                uploadId,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                formatSearch(searchTerm),
                pageSize,
                pageSize * pageIndex,
                sortByList.get(0),
                sortByList.get(1)
        );
    }

    public List<AlbumStats> collectAlbumStats(
            long uploadId,
            String start,
            String end,
            String searchTerm,
            int pageSize,
            int pageIndex,
            String sortBy
    ) {
        List<String> sortByList = getSortBy(sortBy);
        return this.songDataDao.collectAlbumStats(
                uploadId,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                formatSearch(searchTerm),
                pageSize,
                pageSize * pageIndex,
                sortByList.get(0),
                sortByList.get(1)
        );
    }

    public List<ArtistStats> collectArtistStats(
            long uploadId,
            String start,
            String end,
            String searchTerm,
            int pageSize,
            int pageIndex,
            String sortBy
    ) {
        List<String> sortByList = getSortBy(sortBy);
        return this.songDataDao.collectArtistStats(
                uploadId,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                formatSearch(searchTerm),
                pageSize,
                pageSize * pageIndex,
                sortByList.get(0),
                sortByList.get(1)
        );
    }

    public List<CombinedStats> collectAllStats(
            long uploadId,
            String start,
            String end,
            int pageSize,
            int pageIndex,
            String sortBy
    ) {
        List<String> sortByList = getSortBy(sortBy);
        return this.songDataDao.collectAllStats(
                uploadId,
                Timestamp.valueOf(start),
                Timestamp.valueOf(end),
                pageSize,
                pageSize * pageIndex,
                sortByList.get(0),
                sortByList.get(1)
        );
    }

    private List<String> getSortBy(String sortBy) {
        List<String> output = new java.util.ArrayList<>(List.of());
        for (String splitItem : sortBy.split(",")) {
            if (SORT_OPTIONS.contains(splitItem)) {
                output.add(splitItem);
            }
        }
        while (output.size() < 2) {
            for (String item : SORT_OPTIONS) {
                if (!output.contains(item)) {
                    output.add(item);
                }
            }
        }
        return output;
    }

    private String formatSearch(String searchTerm) {
        return "%"+searchTerm+"%";
    }
}
