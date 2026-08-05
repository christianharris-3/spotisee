package com.spotisee.app.models.dao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SingleSong {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
    private String trackName;
    private String albumName;
    private String artistName;
}
