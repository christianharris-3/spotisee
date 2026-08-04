package com.spotisee.app.managers;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.exceptions.SelectionNotOwnedException;
import com.spotisee.app.exceptions.UploadNotOwnedException;
import com.spotisee.app.models.User;

import java.util.List;
import java.util.Optional;

public class UserValidationManager {

    private final AuthDao authDao;

    public UserValidationManager(AuthDao authDao) {
        this.authDao = authDao;
    }

    public void validateUserHasUpload(User user, long uploadId) throws UploadNotOwnedException {
        List<Long> uploadIds = authDao.getUserUploads(user.getUserId());
        if (!uploadIds.contains(uploadId)) {
            throw new UploadNotOwnedException();
        }
    }

    public void validateUserHasSelection(User user, long selectionId) throws UploadNotOwnedException {
        List<Long> selectionIds = authDao.getUserSelections(user.getUserId());
        if (!selectionIds.contains(selectionId)) {
            throw new SelectionNotOwnedException();
        }
    }

    public void validateUserHasSelectionItem(User user, long selectionId, long selectionItemId) throws UploadNotOwnedException {
        Optional<Long> matches = authDao.checkUserSelectionItemMatches(user.getUserId(), selectionId, selectionItemId);
        if (matches.isEmpty()) {
            throw new SelectionNotOwnedException();
        }
    }
}
