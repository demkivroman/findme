package org.demkiv.domain.service;

import org.demkiv.web.model.form.PersonPhotoForm;

public interface PhotoService {

    void addPhoto(PersonPhotoForm personPhotoForm);
    void deletePhoto(String id);
}
