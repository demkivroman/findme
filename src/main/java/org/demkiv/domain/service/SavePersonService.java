package org.demkiv.domain.service;

import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.web.model.PersonForm;

public class SavePersonService implements EntitySaver<PersonForm, Boolean> {
    @Override
    public Boolean saveEntity(PersonForm entity) {
        return false;
    }
}
