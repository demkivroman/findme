package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.persistance.dao.FinderRepository;
import org.demkiv.persistance.model.dto.FinderContactsDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("finderService")
@RequiredArgsConstructor
@Slf4j
public class FinderServiceImpl implements EntityFinder<Long, Optional<?>> {

    private final FinderRepository finderRepository;

    @Override
    public Optional<?> findEntity(Long id) {
        Optional<FinderContactsDTO> foundInfo = finderRepository.findPhoneAndEmailById(id);
        log.info("Found finder contact info with id {}", id);
        return foundInfo;
    }
}
