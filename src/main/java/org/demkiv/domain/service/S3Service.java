package org.demkiv.domain.service;

import org.demkiv.domain.model.S3UploaderModel;

public interface S3Service {

    void upload(S3UploaderModel model);
    void deletePhotoFromS3(String photoUrl);
}
