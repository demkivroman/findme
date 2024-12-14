package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityPersist;
import org.demkiv.domain.service.PersonService;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class PersonServiceImpl implements EntityPersist<PersonForm, Optional<?>>, PersonService {
    private final SaveUpdateService<PersonForm, Optional<?>> service;
    private final QueryRepository queryRepository;

    @Autowired
    public PersonServiceImpl(
            @Qualifier("persistPerson") SaveUpdateService<PersonForm, Optional<?>> service,
            QueryRepository queryRepository) {
        this.service = service;
        this.queryRepository = queryRepository;
    }

    @Override
    public Optional<?> saveEntity(PersonForm entity) {
        return service.saveEntity(entity);
    }

    @Override
    public Optional<?> updateEntity(PersonForm entity) {
        return service.updateEntity(entity);
    }

    @Override
    @Transactional
    public PersonResponseModel<?> getDetailedPersonInfo(String personId) {
        PersonResponseModel<?> foundInfo = queryRepository.getDetailedPersonInfoFromDB(personId);
        log.info("Detailed person information retrieved from DB. ID - {}", personId);
        return foundInfo;
    }

    @Override
    @Transactional
    public List<?> getRandomPersons(int count) {
        List<Long> ids = queryRepository.getPersonIds();
        Set<Long> idSetForCount = randomIds(ids, count);
        return queryRepository.getPersonsDataAndThumbnails(idSetForCount);
    }

    private Set<Long> randomIds(List<Long> ids, int count) {
        Set<Long> personIds = new HashSet<>();

        while (personIds.size() < count && personIds.size() < ids.size()) {
            int randomIndex = getRandomDiceNumber(ids.size());
            personIds.add(ids.get(randomIndex));
        }

        return personIds;
    }

    public int getRandomDiceNumber(int count)
    {
        return (int) (Math.random() * count);
    }
}
