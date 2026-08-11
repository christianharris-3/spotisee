package com.spotisee.app.models.dao;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class UploadInfo {
    private long uploadId;
    private String uploadName;
    private long itemCount;
    private Timestamp startDate;
    private Timestamp endDate;
}
