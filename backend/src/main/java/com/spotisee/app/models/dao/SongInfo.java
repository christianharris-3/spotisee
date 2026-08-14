package com.spotisee.app.models.dao;

import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class SongInfo {
    private long uploadId;
    private Timestamp endTime;
    private int msPlayed;
    private String trackName;
    private String albumName;
    private String artistName;
    private String spotifyTrackUri;
}
