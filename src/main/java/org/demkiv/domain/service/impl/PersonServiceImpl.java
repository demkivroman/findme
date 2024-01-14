package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.service.PersonService;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonDetailedModel;
import org.demkiv.web.model.PersonForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
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
    public PersonDetailedModel<?,?,?> getDetailedPersonInfo(String personId) {
        PersonDetailedModel<?,?,?> foundInfo = queryRepository.getDetailedPersonInfoFromDB(personId);
        log.info("Detailed person information retrieved from DB. ID - {}", personId);
        return foundInfo;
    }
}
