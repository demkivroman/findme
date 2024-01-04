package org.demkiv.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonFinder implements EntityFinder<String, PersonFoundForm> {
    private final QueryRepository repository;

    @Override
    public List<PersonFoundForm> findEntity(String information) {
        List<PersonFoundForm> foundPersons = repository.findPersons(information, information);
        log.info("Searching! For item [{}] found results {}", information, foundPersons);
        return foundPersons;
    }
}
