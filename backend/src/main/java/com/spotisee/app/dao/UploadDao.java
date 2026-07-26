package com.spotisee.app.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface UploadDao {

    @SqlUpdate("""
        INSERT INTO Uploads (userId)
        VALUES (:userId);
    """)
    @GetGeneratedKeys
    long createUpload(@Bind("userId") long userId);

}
