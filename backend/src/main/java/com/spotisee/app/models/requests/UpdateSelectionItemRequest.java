package com.spotisee.app.models.requests;

import com.spotisee.app.models.enums.GraphType;
import lombok.Data;

import java.time.Instant;

@Data
public class UpdateSelectionItemRequest {
    private Instant startDate;
    private Instant endDate;
}
