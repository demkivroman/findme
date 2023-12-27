package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    @Override
    <S extends Person> S save(S entity);

    @Override
    Optional<Person> findById(Long aLong);
}
