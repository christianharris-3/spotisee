package com.spotisee.app.models.dao;

import com.spotisee.app.models.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SelectionItem {
    private long selectionItemId;
    private long selectionId;

    private String trackName;
    private String albumName;
    private String artistName;

    private ItemType itemType;

    private Timestamp startDate;
    private Timestamp endDate;
}
