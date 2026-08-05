package com.spotisee.app.models.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GraphLineData {
    private String trackName;
    private String albumName;
    private String artistName;

    private String itemType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<GraphLinePointData> pointData;
}
