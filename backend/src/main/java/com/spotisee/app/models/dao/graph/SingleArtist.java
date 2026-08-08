package com.spotisee.app.models.dao.graph;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SingleArtist {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
    private String artistName;
}
