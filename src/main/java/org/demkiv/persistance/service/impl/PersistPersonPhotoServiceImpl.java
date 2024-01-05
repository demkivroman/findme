package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import org.demkiv.persistance.dao.PhotoRepository;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonPhotoForm;
import org.springframework.stereotype.Service;

@Service("photoService")
@AllArgsConstructor
public class PersistPersonPhotoServiceImpl implements PersistService<PersonPhotoForm, Boolean> {
    private PhotoRepository photoRepository;
    @Override
    public Boolean saveEntity(PersonPhotoForm entity) {

        return null;
    }
}
