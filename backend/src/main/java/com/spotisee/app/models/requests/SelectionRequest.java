package com.spotisee.app.models.requests;

import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.PointFrequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SelectionRequest {
    @NotBlank
    private String selectionTitle;
    @NotNull
    private GraphType graphType;
    @NotNull
    private PointFrequency pointFrequency;
    private Integer pointFrequencyDays;
    private Integer daysSummedPerPoint;
}
