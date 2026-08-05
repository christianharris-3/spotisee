package com.spotisee.app.models.requests;

import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.ItemType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class SelectionItemRequest {
    private String trackName;
    private String albumName;
    private String artistName;
    @NotNull
    private ItemType itemType;
    @NotNull
    private Instant startDate;
    @NotNull
    private Instant endDate;
}
