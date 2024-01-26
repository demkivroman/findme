package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.persistance.dao.QueryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonFinderImpl implements EntityFinder<String, List<?>> {
    private final QueryRepository repository;

    @Override
    public List<?> findEntity(String information) {
        List<?> foundPersons = repository.findPersonsAndPhoto(information, information);
        log.info("Searching! For item [{}] found results {}", information, foundPersons);
        return foundPersons;
    }
}
