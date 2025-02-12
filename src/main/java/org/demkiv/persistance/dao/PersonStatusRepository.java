package org.demkiv.persistance.dao;


import org.demkiv.persistance.entity.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonStatusRepository extends JpaRepository<PersonStatus, Long> {
    @Override
    <S extends PersonStatus> S save(S entity);
}
