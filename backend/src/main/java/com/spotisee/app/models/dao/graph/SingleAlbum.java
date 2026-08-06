package com.spotisee.app.models.dao.graph;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleAlbum extends BaseSingleDataPoint {
    private LocalDateTime endTime;
    private int msPlayed;
    private int listened;
    private String albumName;
    private String artistName;
}
