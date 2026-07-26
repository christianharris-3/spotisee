package com.spotisee.app.managers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotisee.app.dao.UploadDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UploadDataManager {
    private static final Logger log = LoggerFactory.getLogger(UploadDataManager.class);
    private final UploadDao uploadDao;
    private final ObjectMapper objectMapper;

    public UploadDataManager(UploadDao uploadDao) {
        this.uploadDao = uploadDao;
        this.objectMapper = new ObjectMapper();
    }

    public void storeZipFile(ZipInputStream zipInputStream) throws IOException {

        uploadDao.createUpload(3L);

        ZipEntry entry;

        while ((entry = zipInputStream.getNextEntry()) != null) {
            log.info("File loading: {}", entry.getName());

            if (entry.isDirectory()) continue;
            JsonNode json = objectMapper.readValue(zipInputStream, JsonNode.class);
            log.info("File: {}", json);
        }
    }
}
