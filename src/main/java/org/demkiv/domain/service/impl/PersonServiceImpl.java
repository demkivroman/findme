package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.service.PersonService;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PersonServiceImpl implements EntitySaver<PersonForm, Long>, PersonService {
    private final PersistService<PersonForm, Long> service;
    private final QueryRepository queryRepository;

    @Autowired
    public PersonServiceImpl(
            @Qualifier("persistPerson") PersistService<PersonForm, Long> service,
            QueryRepository queryRepository) {
        this.service = service;
        this.queryRepository = queryRepository;
    }

    @Override
    public Long saveEntity(PersonForm entity) {
        return service.saveEntity(entity);
    }

    @Override
    @Transactional
    public PersonResponseModel<?> getDetailedPersonInfo(String personId) {
        PersonResponseModel<?> foundInfo = queryRepository.getDetailedPersonInfoFromDB(personId);
        log.info("Detailed person information retrieved from DB. ID - {}", personId);
        return foundInfo;
    }
}
