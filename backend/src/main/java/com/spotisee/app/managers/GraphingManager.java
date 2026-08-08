package com.spotisee.app.managers;

import com.spotisee.app.dao.GraphingDao;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.dao.SelectionResponse;
import com.spotisee.app.models.dao.graph.BaseSingleDataPoint;
import com.spotisee.app.models.dao.graph.SingleDataPoint;
import com.spotisee.app.models.dao.graph.SingleSong;
import com.spotisee.app.models.enums.ItemType;
import com.spotisee.app.models.enums.PointFrequency;
import com.spotisee.app.models.response.GraphData;
import com.spotisee.app.models.response.GraphLineData;
import com.spotisee.app.models.response.GraphLinePointData;

import java.sql.Timestamp;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

import static com.spotisee.app.models.enums.ItemType.SONG;

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
            case DAILY -> Duration.of(1, ChronoUnit.DAYS);
            case WEEKLY -> Duration.of(7, ChronoUnit.DAYS);
            default -> Duration.of(selection.getDaysSummedPerPoint(), ChronoUnit.DAYS);
        };

        List<GraphLineData> graphLines = new ArrayList<>();

        for (SelectionItem selectionItem : selection.getSelectionItems()) {
            List<SingleDataPoint> songs = getSongs(selectionItem);

            List<Instant> dataPointTimestamps = getDataPointTimestamps(selection, selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

            int lowerPointer = 0;
            int upperPointer = 0;
            int windowValue = 0;
            List<GraphLinePointData> outputPoints = new ArrayList<>();

            for (Instant dataPointTimestamp : dataPointTimestamps) {
                while (lowerPointer < songs.size() && dataPointTimestamp.minus(windowSize).isAfter(songs.get(lowerPointer).getEndTime().toInstant(ZoneOffset.UTC))) {
                    windowValue -= songValues.get(lowerPointer);
                    lowerPointer += 1;
                }
                while (upperPointer < songs.size() && dataPointTimestamp.isAfter(songs.get(upperPointer).getEndTime().toInstant(ZoneOffset.UTC))) {
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
            List<SingleDataPoint> songs = getSongs(selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

            List<GraphLinePointData> outputPoints = new ArrayList<>();

            int songPointer = 0;
            int currentMonth = songs.get(0).getEndTime().getMonthValue();

            while (songPointer < songs.size()) {
                int cummulative_value = 0;
                while (songPointer < songs.size() && songs.get(songPointer).getEndTime().getMonthValue() == currentMonth) {
                    cummulative_value += songValues.get(songPointer);
                    songPointer += 1;
                }
                LocalDateTime lastDate = songs.get(songPointer-1).getEndTime();
                Instant end_of_month = LocalDate.of(
                        lastDate.getYear(),
                        lastDate.getMonth(),
                        lastDate.getMonth().length(lastDate.toLocalDate().isLeapYear())
                ).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
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
            List<SingleDataPoint> songs = getSongs(selectionItem);

            List<Integer> songValues = getSongValues(selection, songs);

            List<GraphLinePointData> outputPoints = new ArrayList<>();

            int songPointer = 0;
            int currentYear = songs.get(0).getEndTime().getYear();

            while (songPointer < songs.size()) {
                int cummulative_value = 0;
                while (songPointer < songs.size() && songs.get(songPointer).getEndTime().getYear() == currentYear) {
                    cummulative_value += songValues.get(songPointer);
                    songPointer += 1;
                }
                LocalDateTime lastDate = songs.get(songPointer-1).getEndTime();
                Instant end_of_month = LocalDate.of(
                        lastDate.getYear(), 12, 31
                ).atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
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

    private List<SingleDataPoint> getSongs(SelectionItem selectionItem) {
        return switch (selectionItem.getItemType()) {
            case SONG -> graphingDao.getSongPoints(
                    Timestamp.valueOf(selectionItem.getStartDate()),
                    Timestamp.valueOf(selectionItem.getEndDate()),
                    selectionItem.getTrackName(),
                    selectionItem.getAlbumName(),
                    selectionItem.getArtistName()
            );
            case ALBUM -> graphingDao.getAlbumPoints(
                    Timestamp.valueOf(selectionItem.getStartDate()),
                    Timestamp.valueOf(selectionItem.getEndDate()),
                    selectionItem.getAlbumName(),
                    selectionItem.getArtistName()
            );
            case ARTIST -> graphingDao.getArtistPoints(
                    Timestamp.valueOf(selectionItem.getStartDate()),
                    Timestamp.valueOf(selectionItem.getEndDate()),
                    selectionItem.getArtistName()
            );
            case COMBINED -> graphingDao.getCombinedPoints(
                    Timestamp.valueOf(selectionItem.getStartDate()),
                    Timestamp.valueOf(selectionItem.getEndDate())
            );
        };
    }

    private static List<Integer> getSongValues(SelectionResponse selection, List<SingleDataPoint> songs) {
        return switch (selection.getGraphType()) {
            case TIME -> songs.stream().map(SingleDataPoint::getMsPlayed).toList();
            case LISTENS -> songs.stream().map(SingleDataPoint::getListened).toList();
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
                startDate = startDate.minusDays(startDate.getDayOfWeek().getValue() - 1);
                break;
            default:
                interval = Duration.of(selection.getPointFrequencyDays(), ChronoUnit.DAYS);
                break;
        }


        while (startDate.isBefore(endDate)) {
            startDate = startDate.plus(interval);
            dataPointTimestamps.add(startDate.toInstant(ZoneOffset.UTC));
        }


        return dataPointTimestamps;
    }
}
