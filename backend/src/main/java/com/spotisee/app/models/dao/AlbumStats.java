package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class AlbumStats {
    private long uploadId;
    private String albumName;
    private String artistName;
    private long totalMsPlayed;
    private int count;
    private int listens;
    private int skips;
}
