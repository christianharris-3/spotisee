package com.spotisee.app.models.dao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UploadInfo {
    private long uploadId;
    private String uploadName;
    private long itemCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
