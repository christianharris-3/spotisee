package com.spotisee.app.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class GraphLinePointData {
    private Instant date;
    private float value;
}
