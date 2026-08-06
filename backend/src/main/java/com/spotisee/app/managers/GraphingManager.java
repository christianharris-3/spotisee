package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.dao.SingleSong;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.PointFrequency;
import com.spotisee.app.models.response.GraphData;
import com.spotisee.app.models.response.GraphLineData;
import com.spotisee.app.models.response.GraphLinePointData;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public GraphData getGraphingData(long userId, long selectionId) {
        SelectionResponse selection = selectionManager.getSelectionItems(userId, selectionId);

        if (selection.getPointFrequency() == PointFrequency.CUSTOM) {
            return getGraphDataCustomFrequency(selection);
        }
    }

    private GraphData getGraphDataCustomFrequency(SelectionResponse selection) {

        TemporalAmount windowSize = Duration.of(selection.getDaysSummedPerPoint(), ChronoUnit.DAYS);

        List<GraphLineData> graphLines = new ArrayList<>();

        for (SelectionItem selectionItem : selection.getSelectionItems()) {
            List<SingleSong> songs = graphingDao.getSongPoints(
                    Timestamp.valueOf(selectionItem.getStartDate()),
                    Timestamp.valueOf(selectionItem.getEndDate()),
                    selectionItem.getTrackName(),
                    selectionItem.getArtistName()
            );

            List<Instant> dataPointTimestamps = getDataPointTimestamps(selection, selectionItem);

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

            graphLines.add(new GraphLineData(
                    selectionItem.getTrackName(),
                    selectionItem.getAlbumName(),
                    selectionItem.getArtistName(),
                    selectionItem.getItemType(),
                    selectionItem.getStartDate(),
                    selectionItem.getEndDate(),
                    outputPoints
            ));
        }

        return new GraphData(
                "title",
                selection.getGraphType(),
                selection.getPointFrequency(),
                selection.getPointFrequencyDays(),
                selection.getDaysSummedPerPoint(),
                graphLines
        );
    }

    private GraphData getGraphingDataSpecificFrequency() {

    }

    private List<Instant> getDataPointTimestamps(SelectionResponse selection, SelectionItem selectionItem) {
        LocalDateTime startDate = selectionItem.getStartDate();
        LocalDateTime endDate = selectionItem.getEndDate().toLocalDate().atStartOfDay().plusDays(1L);
        List<Instant> dataPointTimestamps = new ArrayList<>();

        Duration interval;

        switch (selection.getPointFrequency()) {
            case CUSTOM:
                interval = Duration.of(selection.getPointFrequencyDays(), ChronoUnit.DAYS);
                break;
            case DAILY:
                interval = Duration.of(1, ChronoUnit.DAYS);
                break;
            case WEEKLY:
                interval = Duration.of(7, ChronoUnit.DAYS);
                startDate = startDate.minusDays(startDate.getDayOfWeek().getValue()-1);
                break;
            case MONTHLY:
                LocalDate prevDate = LocalDate.of(startDate.getYear(), startDate.getMonthValue(), 1);
                dataPointTimestamps.add(
                        Instant.from(prevDate)
                );
                while (dataPointTimestamps.getLast().isBefore(Instant.from(endDate))) {
                    if (prevDate.getMonthValue() == 12) {
                        prevDate = LocalDate.of(prevDate.getYear()+1, 1, 1);
                    } else {
                        prevDate = LocalDate.of(prevDate.getYear(), prevDate.getMonthValue()+1, 1);
                    }
                    dataPointTimestamps.add(Instant.from(prevDate));
                }
                return dataPointTimestamps;
            case YEARLY:
                LocalDate prevDate = LocalDate.of(startDate.getYear(), 1, 1);
                dataPointTimestamps.add(
                        Instant.from(prevDate)
                );
                while (dataPointTimestamps.getLast().isBefore(Instant.from(endDate))) {
                    prevDate = LocalDate.of(prevDate.getYear()+1, 1, 1);
                    dataPointTimestamps.add(Instant.from(prevDate));
                }
                return dataPointTimestamps;
        }


        while (startDate.isBefore(endDate)) {
            startDate = startDate.plus(interval);
            dataPointTimestamps.add(Instant.from(startDate));
        }


        return dataPointTimestamps;
    }
}
