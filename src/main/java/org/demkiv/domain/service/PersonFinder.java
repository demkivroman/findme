package org.demkiv.domain.service;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonFinder implements EntityFinder<String, PersonFoundForm> {
    private final QueryRepository repository;

    @Override
    public List<PersonFoundForm> findEntity(String information) {
        return repository.findPersons(information, information);
    }
}
