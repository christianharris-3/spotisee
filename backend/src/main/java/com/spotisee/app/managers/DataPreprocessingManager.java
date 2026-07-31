package com.spotisee.app.managers;

import com.spotisee.app.dao.DataPreProcessingDao;
import com.spotisee.app.models.dao.UploadedSong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataPreprocessingManager {

    private static final Logger log = LoggerFactory.getLogger(DataPreprocessingManager.class);

    private final DataPreProcessingDao dataPreProcessingDao;

    public DataPreprocessingManager(DataPreProcessingDao dataPreProcessingDao) {
        this.dataPreProcessingDao = dataPreProcessingDao;
    }

    public void preProcessUpload(long uploadId) {
        List<UploadedSong> uploadedSongs = dataPreProcessingDao.loadSongUploads(uploadId);
        log.info("UPLOADED SONGS LIST SIZE: {}", uploadedSongs.size());
    }
}
