package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.dao.SingleSong;
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

        if (selection.getPointFrequency() == PointFrequency.MONTHLY) {
            return getGraphingDataMonthly(selection);
        } else if (selection.getPointFrequency() == PointFrequency.YEARLY) {
            return getGraphingDataYearly(selection);
        }
        return getGraphDataCustomFrequency(selection);

    }

    private GraphData getGraphDataCustomFrequency(SelectionResponse selection) {

        TemporalAmount windowSize = switch (selection.getPointFrequency()) {
            case DAILY ->  Duration.of(1, ChronoUnit.DAYS);
            case WEEKLY ->  Duration.of(7, ChronoUnit.DAYS);
            default ->  Duration.of(selection.getDaysSummedPerPoint(), ChronoUnit.DAYS);
        };

        List<GraphLineData> graphLines = new ArrayList<>();

        for (SelectionItem selectionItem : selection.getSelectionItems()) {
            List<SingleSong> songs = getSongs(selectionItem);

            List<Instant> dataPointTimestamps = getDataPointTimestamps(selection, selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

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

            graphLines.add(getGraphLineData(selectionItem, outputPoints));
        }

        return createGraphData(selection, graphLines);
    }

    private GraphData getGraphingDataMonthly(SelectionResponse selection) {
        List<GraphLineData> graphLines = new ArrayList<>();

        for (SelectionItem selectionItem : selection.getSelectionItems()) {
            List<SingleSong> songs = getSongs(selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

            List<GraphLinePointData> outputPoints = new ArrayList<>();

            int songPointer = 0;
            int currentMonth = songs.get(0).getEndTime().getMonthValue();

            while (songPointer < songs.size()) {
                int cummulative_value = 0;
                while (songs.get(songPointer).getEndTime().getMonthValue() == currentMonth) {
                    songPointer += 1;
                    cummulative_value += songValues.get(songPointer);
                }
                LocalDateTime lastDate = songs.get(songPointer).getEndTime();
                Instant end_of_month = Instant.from(LocalDate.of(
                        lastDate.getYear(),
                        lastDate.getMonth(),
                        lastDate.getMonth().length(lastDate.toLocalDate().isLeapYear())
                ).atTime(23, 59, 59));
                currentMonth = (currentMonth % 12) + 1;
                outputPoints.add(
                        new GraphLinePointData(
                                end_of_month,
                                cummulative_value
                        )
                );
            }
            graphLines.add(getGraphLineData(selectionItem, outputPoints));
        }
        return createGraphData(selection, graphLines);
    }

    private GraphData getGraphingDataYearly(SelectionResponse selection) {
        List<GraphLineData> graphLines = new ArrayList<>();

        for (SelectionItem selectionItem : selection.getSelectionItems()) {
            List<SingleSong> songs = getSongs(selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

            List<GraphLinePointData> outputPoints = new ArrayList<>();

            int songPointer = 0;
            int currentYear = songs.get(0).getEndTime().getYear();

            while (songPointer < songs.size()) {
                int cummulative_value = 0;
                while (songs.get(songPointer).getEndTime().getYear() == currentYear) {
                    songPointer += 1;
                    cummulative_value += songValues.get(songPointer);
                }
                LocalDateTime lastDate = songs.get(songPointer).getEndTime();
                Instant end_of_month = Instant.from(LocalDate.of(
                        lastDate.getYear(), 12, 31
                        ).atTime(23, 59, 59));
                currentYear = currentYear + 1;
                outputPoints.add(
                        new GraphLinePointData(
                                end_of_month,
                                cummulative_value
                        )
                );
            }
            graphLines.add(getGraphLineData(selectionItem, outputPoints));
        }
        return createGraphData(selection, graphLines);
    }

    private List<SingleSong> getSongs(SelectionItem selectionItem) {
        return graphingDao.getSongPoints(
                Timestamp.valueOf(selectionItem.getStartDate()),
                Timestamp.valueOf(selectionItem.getEndDate()),
                selectionItem.getTrackName(),
                selectionItem.getArtistName()
        );
    }

    private static List<Integer> getSongValues(SelectionResponse selection, List<SingleSong> songs) {
        return switch (selection.getGraphType()) {
            case TIME -> songs.stream().map(SingleSong::getMsPlayed).toList();
            case LISTENS -> songs.stream().map(SingleSong::getListened).toList();
            default -> songs.stream().map(song -> 1).toList();
        };
    }

    private static GraphLineData getGraphLineData(SelectionItem selectionItem, List<GraphLinePointData> outputPoints) {
        return new GraphLineData(
                selectionItem.getTrackName(),
                selectionItem.getAlbumName(),
                selectionItem.getArtistName(),
                selectionItem.getItemType(),
                selectionItem.getStartDate(),
                selectionItem.getEndDate(),
                outputPoints
        );
    }

    private static GraphData createGraphData(SelectionResponse selection, List<GraphLineData> graphLines) {
        return new GraphData(
                "title",
                selection.getGraphType(),
                selection.getPointFrequency(),
                selection.getPointFrequencyDays(),
                selection.getDaysSummedPerPoint(),
                graphLines
        );
    }

    private List<Instant> getDataPointTimestamps(SelectionResponse selection, SelectionItem selectionItem) {
        LocalDateTime startDate = selectionItem.getStartDate();
        LocalDateTime endDate = selectionItem.getEndDate().toLocalDate().atStartOfDay().plusDays(1L);
        List<Instant> dataPointTimestamps = new ArrayList<>();

        Duration interval;

        switch (selection.getPointFrequency()) {
            case DAILY:
                interval = Duration.of(1, ChronoUnit.DAYS);
                break;
            case WEEKLY:
                interval = Duration.of(7, ChronoUnit.DAYS);
                startDate = startDate.minusDays(startDate.getDayOfWeek().getValue()-1);
                break;
            default:
                interval = Duration.of(selection.getPointFrequencyDays(), ChronoUnit.DAYS);
                break;
        }


        while (startDate.isBefore(endDate)) {
            startDate = startDate.plus(interval);
            dataPointTimestamps.add(Instant.from(startDate));
        }


        return dataPointTimestamps;
    }
}
