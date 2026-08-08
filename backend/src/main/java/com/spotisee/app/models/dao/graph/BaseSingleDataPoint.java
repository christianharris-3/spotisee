package com.spotisee.app.models.dao.graph;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseSingleDataPoint implements SingleDataPoint {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
}
