package com.spotisee.app.models.dao;

import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.PointFrequency;
import lombok.Data;

@Data
public class Selection {
    private long selectionId;
    private long userId;
    private String selectionTitle;
    private GraphType graphType;

    private PointFrequency pointFrequency;
    private Integer pointFrequencyDays;
    private Integer daysSummedPerPoint;
}
