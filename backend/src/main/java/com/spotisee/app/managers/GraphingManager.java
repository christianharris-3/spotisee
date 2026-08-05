package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionResponse;

public class GraphingManager {

    private final GraphingDao graphingDao;
    private final SelectionManager selectionManager;

    public GraphingManager(GraphingDao graphingDao, SelectionManager selectionManager) {
        this.graphingDao = graphingDao;
        this.selectionManager = selectionManager;
    }

    public void getGraphingData(long userId, long selectionId) {
        SelectionResponse selection = selectionManager.getSelectionItems(userId, selectionId);

    }
}
