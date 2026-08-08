package com.spotisee.app.models.requests;

import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.PointFrequency;
import lombok.Data;

@Data
public class UpdateSelectionRequest {
    private String selectionTitle;
    private GraphType graphType;
    private PointFrequency pointFrequency;
    private Integer pointFrequencyDays;
    private Integer daysSummedPerPoint;
}
