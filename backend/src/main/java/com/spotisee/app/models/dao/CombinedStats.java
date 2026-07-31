package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class CombinedStats {
    private long uploadId;
    private long totalMsPlayed;
    private int count;
    private int listens;
}
