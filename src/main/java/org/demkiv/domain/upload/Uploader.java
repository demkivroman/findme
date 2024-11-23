package org.demkiv.domain.upload;

import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.File;

public interface Uploader {
    void uploadPhoto(File source);
    void uploadThumbnail(String source);
    Boolean saveEntity(PersonPhotoForm personPhotoForm);
}
