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
    void test_testing() {

        when(graphingDao.getSongPoints(any(),
                any(),
                anyString(),
                anyString(),
                anyString())).thenReturn(List.of(new SingleSong(getStart().plusDays(10), 0, 1, "", "", "")));
        when(selectionManager.getSelectionItems(1, 1)).thenReturn(getSelection());

        GraphData graphData = graphingManager.getGraphingData(1, 1);


    }

    private SelectionResponse getSelection() {
        return new SelectionResponse(1,
                1,
                "title",
                GraphType.LISTENS,
                PointFrequency.YEARLY,
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
