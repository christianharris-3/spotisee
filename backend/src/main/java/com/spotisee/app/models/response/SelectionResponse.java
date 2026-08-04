package com.spotisee.app.models.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SelectionResponse {
    private long selectionId;
    private long userId;
    private String selectionTitle;
    private List<SelectionItem> selectionItems;
}
