package org.demkiv.domain.upload;

import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.File;

public interface Uploader {
    void uploadPhoto(File source);
    Boolean saveEntity(PersonPhotoForm personPhotoForm, File photoSource);
}
