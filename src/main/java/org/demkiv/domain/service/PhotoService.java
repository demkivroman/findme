package org.demkiv.domain.service;

import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.IOException;

public interface PhotoService {

    void addPhoto(PersonPhotoForm personPhotoForm);
    void deletePhoto(String id);
}
