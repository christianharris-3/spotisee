package com.spotisee.app.models.dao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadedSong {
    private LocalDateTime timestamp;
    private int msPlayed;
    private String trackName;
    private String albumName;
    private String artistName;
    private String spotifyTrackUri;
}
