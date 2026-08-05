package com.spotisee.app.models.dao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SelectionItem {
    private long selectionItemId;
    private long selectionId;

    private String trackName;
    private String albumName;
    private String artistName;

    private String itemType;
    private String graphType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
