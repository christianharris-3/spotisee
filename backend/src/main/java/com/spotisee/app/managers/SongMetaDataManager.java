package com.spotisee.app.managers;

import com.spotisee.app.dao.SongMetaDataDao;
import com.spotisee.app.models.dao.MonthYearPair;
import com.spotisee.app.models.enums.ItemType;
import com.spotisee.app.models.response.YearWithMonths;

import java.util.ArrayList;
import java.util.List;

import static com.spotisee.app.util.EnumCaster.castToItemType;


public class SongMetaDataManager {

    private final SongMetaDataDao songMetaDataDao;

    public SongMetaDataManager(SongMetaDataDao songMetaDataDao) {
        this.songMetaDataDao = songMetaDataDao;
    }

    public List<YearWithMonths> getYearsAvailable(long uploadId, String searchTerm, String itemType) {
        List<MonthYearPair> dateInfo = switch (castToItemType(itemType)) {
            case SONG -> songMetaDataDao.getYearsAvailableSong(uploadId, formatSearch(searchTerm));
            case ALBUM -> songMetaDataDao.getYearsAvailableAlbum(uploadId, formatSearch(searchTerm));
            case ARTIST -> songMetaDataDao.getYearsAvailableArtist(uploadId, formatSearch(searchTerm));
            case COMBINED -> songMetaDataDao.getYearsAvailableCombined(uploadId);
        };
        return convertToYearMap(dateInfo);

    }

    private List<YearWithMonths> convertToYearMap(List<MonthYearPair> monthYearPairs) {
        List<YearWithMonths> yearWithMonths = new ArrayList<>();

        for (MonthYearPair pair : monthYearPairs) {
            boolean yearFound = false;
            for (YearWithMonths yearWithMonth : yearWithMonths) {
                if (yearWithMonth.getYear() == pair.getYear()) {
                    yearFound = true;
                    yearWithMonth.addMonth(pair.getMonth());
                    break;
                }
            }
            if (!yearFound) {
                yearWithMonths.add(
                        new YearWithMonths(pair.getYear(), new ArrayList<>(pair.getMonth()))
                );
            }
        }
        return yearWithMonths;
    }

    private String formatSearch(String searchTerm) {
        return "%"+searchTerm+"%";
    }
}
