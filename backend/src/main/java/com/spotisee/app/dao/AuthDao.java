package com.spotisee.app.dao;

import com.spotisee.app.models.dao.UserFromDb;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthDao {
    @RegisterBeanMapper(UserFromDb.class)
    @SqlQuery("""
            SELECT userId, username, passwordHash
            FROM Users
            WHERE (:username = username);
            """)
    Optional<UserFromDb> getUser(@Bind("username") String username);

    @SqlQuery("""
            SELECT role
            FROM UserRole
            WHERE (:userId = userId);
            """)
    Set<String> getUserRoles(@Bind("userId") long userId);

    @SqlUpdate("""
            INSERT INTO Users (username, passwordHash)
            VALUES (:username, :passwordHash);
            """)
    @GetGeneratedKeys
    long registerUser(@Bind("username") String username, @Bind("passwordHash") String passwordHash);

    @SqlUpdate("""
            INSERT INTO UserRole (userId, role)
            VALUES (:userId, :role);
            """)
    void addUserRole(@Bind("userId") long userId, @Bind("role") String role);

    @SqlQuery("""
            SELECT UploadId FROM Upload
            WHERE :userId = userId;
            """)
    List<Long> getUserUploads(@Bind("userId") long userId);
}
