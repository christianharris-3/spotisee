package com.spotisee.app.dao;

import com.spotisee.app.models.dao.Selection;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.ItemType;
import com.spotisee.app.models.enums.PointFrequency;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface SelectionDao {
    @SqlUpdate("""
            INSERT INTO Selection (userId, selectionTitle, graphType, pointFrequency, pointFrequencyDays, daysSummedPerPoint)
            VALUES (:userId, :selectionTitle, :graphType, :pointFrequency, :pointFrequencyDays, :daysSummedPerPoint);
            """)
    @GetGeneratedKeys
    long createSelection(
            @Bind("userId") long userId,
            @Bind("selectionTitle") String selectionTitle,
            @Bind("graphType") GraphType graphType,
            @Bind("pointFrequency") PointFrequency pointFrequency,
            @Bind("pointFrequencyDays") Integer pointFrequencyDays,
            @Bind("daysSummedPerPoint") Integer daysSummedPerPoint
    );

    @RegisterBeanMapper(Selection.class)
    @SqlQuery("""
            SELECT selectionId, userId, selectionTitle, graphType, pointFrequency, pointFrequencyDays, daysSummedPerPoint
            FROM Selection
            WHERE (:userId = userId);
            """)
    List<Selection> getUserSelections(@Bind("userId") long userId);

    @RegisterBeanMapper(Selection.class)
    @SqlQuery("""
            SELECT selectionId, userId, selectionTitle, graphType, pointFrequency, pointFrequencyDays, daysSummedPerPoint
            FROM Selection
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    Optional<Selection> getSelection(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            UPDATE Selection
            SET selectionTitle = COALESCE(:selectionTitle, selectionTitle),
                graphType = COALESCE(:graphType, graphType)
                pointFrequency = COALESCE(:pointFrequency, pointFrequency)
                pointFrequencyDays = COALESCE(:pointFrequencyDays, pointFrequencyDays)
                daysSummedPerPoint = COALESCE(:daysSummedPerPoint, daysSummedPerPoint)
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    void updateSelection(
            @Bind("userId") long userId,
            @Bind("selectionId") long selectionId,
            @Bind("selectionTitle") String selectionTitle,
            @Bind("graphType") GraphType graphType,
            @Bind("pointFrequency") PointFrequency pointFrequency,
            @Bind("pointFrequencyDays") Integer pointFrequencyDays,
            @Bind("daysSummedPerPoint") Integer daysSummedPerPoint
    );

    @SqlUpdate("""
            DELETE FROM Selection
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    void deleteSelection(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            INSERT INTO SelectionItem (selectionId, trackName, albumName, artistName, itemType, startDate, endDate)
            VALUES (:selectionId, :trackName, :albumName, :artistName, :itemType, :startDate, :endDate);
            """)
    @GetGeneratedKeys
    long createSelectionItem(
            @Bind("selectionId") long selectionId,
            @Bind("trackName") String trackName,
            @Bind("albumName") String albumName,
            @Bind("artistName") String artistName,
            @Bind("itemType") ItemType itemType,
            @Bind("startDate") Timestamp startDate,
            @Bind("endDate") Timestamp endDate
    );

    @RegisterBeanMapper(SelectionItem.class)
    @SqlQuery("""
            SELECT
                SelectionItem.selectionItemId,
                SelectionItem.SelectionId,
                SelectionItem.trackName,
                SelectionItem.albumName,
                SelectionItem.artistName,
                SelectionItem.itemType,
                SelectionItem.startDate,
                SelectionItem.endDate
            FROM Selection JOIN SelectionItem
            ON Selection.userId = SelectionItem.selectionId
            WHERE (Selection.userId = :userId) AND (Selection.selectionId = :selectionId);
            """)
    List<SelectionItem> getSelectionItems(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            UPDATE SelectionItem
            SET startDate = COALESCE(:startDate, startDate),
                endDate = COALESCE(:endDate, endDate)
            WHERE (:selectionItemId = selectionItemId);
            """)
    void updateSelectionItem(
            @Bind("selectionItemId") long selectionItemId,
            @Bind("startDate") Timestamp startDate,
            @Bind("endDate") Timestamp endDate
    );

    @SqlUpdate("""
            DELETE FROM SelectionItem
            WHERE (:selectionItemId = selectionItemId);
            """)
    void deleteSelectionItem(@Bind("selectionItemId") long selectionItemId);
}
