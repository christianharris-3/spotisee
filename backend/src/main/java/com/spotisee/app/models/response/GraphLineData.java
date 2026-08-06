package com.spotisee.app.models.response;

import com.spotisee.app.models.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class GraphLineData {
    private String trackName;
    private String albumName;
    private String artistName;

    private ItemType itemType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private List<GraphLinePointData> pointData;
}
