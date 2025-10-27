package org.demkiv.domain.service;

import org.demkiv.domain.model.S3UploaderModel;

import java.io.File;

public interface S3Service {

    void upload(String key, File photo);
    void deletePhotoFromS3(String photoUrl);
}
