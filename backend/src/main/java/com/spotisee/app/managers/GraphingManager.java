package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.dao.SingleSong;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.response.GraphLinePointData;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

public class GraphingManager {

    private final GraphingDao graphingDao;
    private final SelectionManager selectionManager;

    public GraphingManager(GraphingDao graphingDao, SelectionManager selectionManager) {
        this.graphingDao = graphingDao;
        this.selectionManager = selectionManager;
    }

    public void getGraphingData(long userId, long selectionId) {
        SelectionResponse selection = selectionManager.getSelectionItems(userId, selectionId);

        List<Instant> dataPointTimestamps = getDataPointTimestamps(selection);

        TemporalAmount windowSize = Duration.of(selection.getDaysSummedPerPoint(), ChronoUnit.DAYS);

        for (SelectionItem item : selection.getSelectionItems()) {
            List<SingleSong> songs = graphingDao.getSongPoints(
                    Timestamp.valueOf(item.getStartDate()),
                    Timestamp.valueOf(item.getEndDate()),
                    item.getTrackName(),
                    item.getArtistName()
            );

            List<Integer> songValues = switch (selection.getGraphType()) {
                case TIME -> songs.stream().map(SingleSong::getMsPlayed).toList();
                case LISTENS -> songs.stream().map(SingleSong::getListened).toList();
                case COUNT -> songs.stream().map(song -> 1).toList();
            };

            int lowerPointer = 0;
            int upperPointer = 0;
            int windowValue = 0;
            List<GraphLinePointData> outputPoints = new ArrayList<>();


            for (Instant dataPointTimestamp : dataPointTimestamps) {
                while (lowerPointer < songs.size() && dataPointTimestamp.minus(windowSize).isBefore(Instant.from(songs.get(lowerPointer).getEndTime()))) {
                    windowValue -= songValues.get(lowerPointer);
                    lowerPointer += 1;
                }
                while (upperPointer < songs.size() && dataPointTimestamp.isBefore(Instant.from(songs.get(upperPointer).getEndTime()))) {
                    windowValue += songValues.get(upperPointer);
                    upperPointer += 1;
                }
                outputPoints.add(
                        new GraphLinePointData(
                                dataPointTimestamp,
                                windowValue
                        )
                );
            }

        }


        //selection.getDaysSummedPerPoint();

//        dataPointTimestamps.get(0).
        // get the datapoints, have fun

    }

    private List<Instant> getDataPointTimestamps(SelectionResponse selection) {

    }
}
