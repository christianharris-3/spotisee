package com.spotisee.app.managers;

import com.spotisee.app.dao.SelectionDao;
import com.spotisee.app.models.dao.Selection;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.ItemType;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class SelectionManager {

    private final SelectionDao selectionDao;

    public SelectionManager(SelectionDao selectionDao) {
        this.selectionDao = selectionDao;
    }

    public long createSelection(long userId, String selectionTitle) {
        return selectionDao.createSelection(userId, selectionTitle);
    }

    public List<Selection> getSelections(long userId) {
        return selectionDao.getUserSelections(userId);
    }

    public void updateSelection(long userId, long selectionId, String selectionTitle) {
        selectionDao.updateSelection(userId, selectionId, selectionTitle);
    }

    public void deleteSelection(long userId, long selectionId) {
        selectionDao.deleteSelection(userId, selectionId);
    }

    public long createSelectionItem(
            long selectionId,
            String trackName,
            String albumName,
            String artistName,
            ItemType itemType,
            GraphType graphType,
            Instant startDate,
            Instant endDate
    ) {
        return selectionDao.createSelectionItem(
                selectionId,
                trackName,
                albumName,
                artistName,
                itemType,
                graphType,
                toTimestamp(startDate),
                toTimestamp(endDate)
        );
    }

    public SelectionResponse getSelectionItems(long userId, long selectionId) {
        Optional<Selection> selection = selectionDao.getSelection(userId, selectionId);
        if (selection.isEmpty()) {
            throw new RuntimeException(String.format("Selection not found: %s", selectionId));
        }

        return new SelectionResponse(
                selectionId,
                userId,
                selection.get().getSelectionTitle(),
                selectionDao.getSelectionItems(userId, selectionId)
        );
    }

    public void updateSelectionItem(long selectionItemId, GraphType graphType, Instant startDate, Instant endDate) {
        selectionDao.updateSelectionItem(
                selectionItemId,
                graphType,
                toTimestamp(startDate),
                toTimestamp(endDate)
        );
    }

    public void deleteSelectionItem(long selectionItemId) {
        selectionDao.deleteSelectionItem(selectionItemId);
    }

    private Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
