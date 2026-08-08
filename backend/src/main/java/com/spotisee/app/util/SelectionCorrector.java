package com.spotisee.app.util;

import com.spotisee.app.models.enums.PointFrequency;
import com.spotisee.app.models.requests.SelectionRequest;
import com.spotisee.app.models.requests.UpdateSelectionRequest;

public class SelectionCorrector {
    public static void correctSelectionRequest(SelectionRequest selection) {
        if (selection.getPointFrequency().equals(PointFrequency.CUSTOM)) {
            if (selection.getPointFrequencyDays() == null) {
                selection.setPointFrequencyDays(7);
            }
            if (selection.getDaysSummedPerPoint() == null) {
                selection.setDaysSummedPerPoint(selection.getPointFrequencyDays());
            }
        }
    }
    public static boolean correctUpdateSelectionRequest(UpdateSelectionRequest selection) {
        if (selection.getPointFrequency().equals(PointFrequency.CUSTOM)) {
            if (selection.getPointFrequencyDays() == null) {
                return false;
            }
            return selection.getDaysSummedPerPoint() != null;
        }
        return true;
    }
}
