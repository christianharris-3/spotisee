package com.spotisee.app.models.dao;

import lombok.Data;

@Data
public class UserFromDb {
    private long userId;
    private long activeUploadId;
    private String username;
    private String passwordHash;
}
