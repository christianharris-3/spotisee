package com.spotisee.app.models.response;

import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.PointFrequency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GraphData {
    private String graphTitle;
    private GraphType graphType;

    private PointFrequency pointFrequency;
    private int pointFrequencyDays;
    private int daysSummedPerPoint;
    private List<GraphLineData> graphLineData;
}
