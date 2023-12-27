package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Finder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinderRepository extends JpaRepository<Finder, Long> {
    @Override
    <S extends Finder> S save(S finder);
    @Override
    Optional<Finder> findById(Long id);
}
