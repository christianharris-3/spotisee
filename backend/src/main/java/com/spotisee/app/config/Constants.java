package com.spotisee.app.config;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Constants {

    public static final String TIMESTAMP_LOWER_BOUND = "1970-01-01 00:00:00";
    public static final String TIMESTAMP_UPPER_BOUND = "2040-01-01 00:00:00";
    public static final String PAGE_SIZE = "100";

    public static final String DEFAULT_SORT = "totalMsPlayed";
    public static final Set<String> SORT_OPTIONS = Set.of("listens", "count", "totalMsPlayed");
}
