package org.demkiv.domain.service;

import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class PersonService implements EntitySaver<PersonForm, Long> {
    private final PersistService<PersonForm, Long> service;

    public PersonService(@Qualifier("persistPerson") PersistService<PersonForm, Long> service) {
        this.service = service;
    }

    @Override
    public Long saveEntity(PersonForm entity) {
        return service.saveEntity(entity);
    }
}
