package org.demkiv.domain.upload;

import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.File;

public interface Uploader {
    void uploadPhoto(File source);
    void uploadThumbnail(File source);
    Boolean saveEntity(PersonPhotoForm personPhotoForm);
    Boolean saveThumbnailEntity(PersonPhotoForm personPhotoForm, File thumbnailSource);
}
