package com.spotisee.app.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class YearWithMonths {
    private int year;
    private List<Integer> months;

    public void addMonth(Integer month) {
        months.add(month);
    }
}
