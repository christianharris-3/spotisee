package com.spotisee.app.dao;

import com.spotisee.app.models.dao.Selection;
import com.spotisee.app.models.dao.SelectionItem;
import com.spotisee.app.models.enums.GraphType;
import com.spotisee.app.models.enums.ItemType;
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
            INSERT INTO Selection (userId, selectionTitle)
            VALUES (:userId, :selectionTitle);
            """)
    @GetGeneratedKeys
    long createSelection(@Bind("userId") long userId, @Bind("selectionTitle") String selectionTitle);

    @RegisterBeanMapper(Selection.class)
    @SqlQuery("""
            SELECT selectionId, userId, selectionTitle
            FROM Selection
            WHERE (:userId = userId);
            """)
    List<Selection> getUserSelections(@Bind("userId") long userId);

    @RegisterBeanMapper(Selection.class)
    @SqlQuery("""
            SELECT selectionId, userId, selectionTitle
            FROM Selection
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    Optional<Selection> getSelection(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            UPDATE Selection
            SET selectionTitle = :selectionTitle
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    void updateSelection(
            @Bind("userId") long userId,
            @Bind("selectionId") long selectionId,
            @Bind("selectionTitle") String selectionTitle
    );

    @SqlUpdate("""
            DELETE FROM Selection
            WHERE (:userId = userId) AND (:selectionId = selectionId);
            """)
    void deleteSelection(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            INSERT INTO SelectionItem (selectionId, trackName, albumName, artistName, itemType, graphType, startDate, endDate)
            VALUES (:selectionId, :trackName, :albumName, :artistName, :itemType, :graphType, :startDate, :endDate);
            """)
    @GetGeneratedKeys
    long createSelectionItem(
            @Bind("selectionId") long selectionId,
            @Bind("trackName") String trackName,
            @Bind("albumName") String albumName,
            @Bind("artistName") String artistName,
            @Bind("itemType") ItemType itemType,
            @Bind("graphType") GraphType graphType,
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
                SelectionItem.graphType,
                SelectionItem.startDate,
                SelectionItem.endDate
            FROM Selection JOIN SelectionItem
            ON Selection.userId = SelectionItem.selectionId
            WHERE (Selection.userId = :userId) AND (Selection.selectionId = :selectionId);
            """)
    List<SelectionItem> getSelectionItems(@Bind("userId") long userId, @Bind("selectionId") long selectionId);

    @SqlUpdate("""
            UPDATE SelectionItem
            SET graphType = COALESCE(:graphType, graphType),
                startDate = COALESCE(:startDate, graphType),
                endDate = COALESCE(:endDate, graphType)
            WHERE (:selectionItemId = selectionItemId);
            """)
    void updateSelectionItem(
            @Bind("selectionItemId") long selectionItemId,
            @Bind("graphType") GraphType graphType,
            @Bind("startDate") Timestamp startDate,
            @Bind("endDate") Timestamp endDate
    );

    @SqlUpdate("""
            DELETE FROM SelectionItem
            WHERE (:selectionItemId = selectionItemId);
            """)
    void deleteSelectionItem(@Bind("selectionItemId") long selectionItemId);
}
