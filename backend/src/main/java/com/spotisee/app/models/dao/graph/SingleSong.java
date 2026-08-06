package com.spotisee.app.models.dao.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SingleSong implements SingleDataPoint {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
    private String trackName;
    private String albumName;
    private String artistName;
}
