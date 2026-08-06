package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.dao.graph.SingleSong;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.ItemType;
import com.spotisee.app.models.enums.PointFrequency;
import com.spotisee.app.models.response.GraphData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraphingManagerTest {

    @Mock
    GraphingDao graphingDao;

    @Mock
    SelectionManager selectionManager;

    @InjectMocks
    GraphingManager graphingManager;

    @Test
    void graphingManagerYearlyTest() {

        when(graphingDao.getSongPoints(any(),
                any(),
                anyString(),
                anyString(),
                anyString())).thenReturn(List.of(new SingleSong(getStart().plusDays(10), 0, 1, "", "", "")));
        when(selectionManager.getSelectionItems(1, 1)).thenReturn(getSelection(PointFrequency.YEARLY));

        GraphData graphData = graphingManager.getGraphingData(1, 1);

        assertEquals(1, graphData.getGraphLineData().size());
        assertEquals(1, graphData.getGraphLineData().get(0).getPointData().size());
        assertEquals(1, graphData.getGraphLineData().get(0).getPointData().get(0).getValue());
    }

    @Test
    void graphingManagerMonthlyTest() {
        when(graphingDao.getSongPoints(any(), any(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(
                        new SingleSong(getStart().plusDays(15), 0, 1, "", "", ""),
                        new SingleSong(getStart().plusDays(45), 0, 2, "", "", ""),
                        new SingleSong(getStart().plusDays(75), 0, 3, "", "", "")
                ));
        when(selectionManager.getSelectionItems(1, 1)).thenReturn(getSelection(PointFrequency.MONTHLY));

        GraphData graphData = graphingManager.getGraphingData(1, 1);

        assertEquals(1, graphData.getGraphLineData().size());
        assertEquals(3, graphData.getGraphLineData().get(0).getPointData().size());
        assertEquals(1, graphData.getGraphLineData().get(0).getPointData().get(0).getValue());
        assertEquals(2, graphData.getGraphLineData().get(0).getPointData().get(1).getValue());
        assertEquals(3, graphData.getGraphLineData().get(0).getPointData().get(2).getValue());
    }

    @Test
    void graphingManagerWeekly() {
        when(graphingDao.getSongPoints(any(), any(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(
                        new SingleSong(getStart().plusDays(3), 0, 1, "", "", ""),
                        new SingleSong(getStart().plusDays(10), 0, 2, "", "", ""),
                        new SingleSong(getStart().plusDays(50), 0, 3, "", "", "")
                ));
        when(selectionManager.getSelectionItems(1, 1)).thenReturn(getSelection(PointFrequency.WEEKLY));

        GraphData graphData = graphingManager.getGraphingData(1, 1);

        assertEquals(1, graphData.getGraphLineData().size());
        assertEquals(106, graphData.getGraphLineData().get(0).getPointData().size());
        assertEquals(1, graphData.getGraphLineData().get(0).getPointData().get(1).getValue());
        assertEquals(2, graphData.getGraphLineData().get(0).getPointData().get(2).getValue());
        assertEquals(3, graphData.getGraphLineData().get(0).getPointData().get(8).getValue());
    }

    private SelectionResponse getSelection(PointFrequency pointFrequency) {
        return new SelectionResponse(1,
                1,
                "title",
                GraphType.LISTENS,
                pointFrequency,
                null,
                null,
                List.of(new SelectionItem(1,
                        1,
                        "track",
                        "album",
                        "artist",
                        ItemType.SONG,
                        getStart(),
                        getEnd())));
    }

    private LocalDateTime getStart() {
        return LocalDate.of(2023, 1, 1).atStartOfDay();
    }

    private LocalDateTime getEnd() {
        return LocalDate.of(2025, 1, 1).atStartOfDay();
    }
}
