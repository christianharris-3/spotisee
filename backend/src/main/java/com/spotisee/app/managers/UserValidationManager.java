package com.spotisee.app.managers;

import com.spotisee.app.dao.AuthDao;
import com.spotisee.app.exceptions.UploadNotOwnedException;
import com.spotisee.app.models.User;

import java.util.List;

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
}
