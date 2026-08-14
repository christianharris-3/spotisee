package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class ArtistStats {
    private long uploadId;
    private int preSearchIndex;
    private String artistName;
    private long totalMsPlayed;
    private int count;
    private int listens;
    private int skips;
}
