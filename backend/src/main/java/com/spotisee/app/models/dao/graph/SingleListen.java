package com.spotisee.app.models.dao.graph;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
public class SingleListen implements SingleDataPoint {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
}
