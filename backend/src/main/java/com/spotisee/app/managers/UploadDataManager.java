package com.spotisee.app.managers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spotisee.app.dao.UploadDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
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

    public long storeZipFile(ZipInputStream zipInputStream) throws IOException {

        long uploadId = uploadDao.createUpload(3L);

        ZipEntry entry;

        try {
            while ((entry = zipInputStream.getNextEntry()) != null) {

                if (entry.isDirectory()) continue;
                JsonNode json = objectMapper.readValue(zipInputStream, JsonNode.class);

                log.info("Loading {} items from file: {}", json.size(), entry.getName());

                for (JsonNode song : json) {
                    storeUploadItem(uploadId, song);
                }
                log.info("Finished Loading {}", entry.getName());
            }
        } catch (IOException e) {
            log.error("Thing threw an error : {} : {}", e.getMessage(), e.getLocalizedMessage());
            if (!Objects.equals(e.getMessage(), "Stream closed")) {
                throw e;
            }
        }
        return uploadId;
    }

    private void storeUploadItem(long uploadId, JsonNode song) {
        uploadDao.createUploadItem(
                uploadId,
                loadJsonValueLocalDateTime(song, "ts"),
                loadJsonValueString(song, "platform"),
                loadJsonValueInt(song, "ms_played"),
                loadJsonValueString(song, "conn_country"),
                loadJsonValueString(song, "ip_addr"),
                loadJsonValueString(song, "master_metadata_track_name"),
                loadJsonValueString(song, "master_metadata_album_artist_name"),
                loadJsonValueString(song, "master_metadata_album_album_name"),
                loadJsonValueString(song, "spotify_track_uri"),
                loadJsonValueString(song, "episode_name"),
                loadJsonValueString(song, "episode_show_name"),
                loadJsonValueString(song, "spotify_episode_uri"),
                loadJsonValueString(song, "audiobook_title"),
                loadJsonValueString(song, "audiobook_uri"),
                loadJsonValueString(song, "audiobook_chapter_uri"),
                loadJsonValueString(song, "audiobook_chapter_title"),
                loadJsonValueString(song, "reason_start"),
                loadJsonValueString(song, "reason_end"),
                loadJsonValueBoolean(song, "shuffle"),
                loadJsonValueBoolean(song, "skipped"),
                loadJsonValueBoolean(song, "offline"),
                loadJsonValueLocalDateTime(song, "offline_timestamp"),
                loadJsonValueBoolean(song, "incognito_mode")
        );
    }

    private String loadJsonValueString(JsonNode node, String item) {
        JsonNode value = node.get(item);
        if (value.isNull()) return null;
        return value.asText();
    }

    private Integer loadJsonValueInt(JsonNode node, String item) {
        JsonNode value = node.get(item);
        if (value.isNull()) return null;
        return value.asInt();
    }

    private Boolean loadJsonValueBoolean(JsonNode node, String item) {
        JsonNode value = node.get(item);
        if (value.isNull()) return null;
        return value.asBoolean();
    }

    private LocalDateTime loadJsonValueLocalDateTime(JsonNode node, String item) {
        String value = loadJsonValueString(node, item);
        if (value == null) return null;
        value = value.replace("Z", "");
        try {
            return LocalDateTime.parse(value);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
}