package com.spotisee.app.models.dao.graph;

import java.time.LocalDateTime;

public interface SingleDataPoint {
    LocalDateTime getEndTime();
    int getMsPlayed();
    int getListened();
}
