package org.demkiv.domain.service.impl;

import org.demkiv.domain.architecture.EntityFinder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("finderService")
public class FinderServiceImpl implements EntityFinder<Long, Optional<?>> {
    @Override
    public Optional<?> findEntity(Long information) {
        return null;
    }
}
