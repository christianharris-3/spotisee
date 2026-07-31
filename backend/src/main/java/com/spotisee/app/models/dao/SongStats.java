package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class SongStats {
    private long uploadId;
    private String trackName;
    private String albumName;
    private String artistName;
    private long totalMsPlayed;
    private int count;
    private int listens;
}
