package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class CombinedStats {
    private long uploadId;
    private int preSearchIndex;
    private long totalMsPlayed;
    private int count;
    private int listens;
    private int skips;
}
