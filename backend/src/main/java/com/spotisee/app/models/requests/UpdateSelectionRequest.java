package com.spotisee.app.models.requests;

import com.spotisee.app.models.enums.GraphType;
import lombok.Data;

@Data
public class UpdateSelectionRequest {
    private String selectionTitle;
    private GraphType graphType;
}
