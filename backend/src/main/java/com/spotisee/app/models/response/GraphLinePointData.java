package com.spotisee.app.models.response;

import lombok.Data;

import java.time.Instant;

@Data
public class GraphLinePointData {
    private Instant date;
    private float value;
}
