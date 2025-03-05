package org.demkiv.persistance.dao;


import org.demkiv.persistance.entity.PersonStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PersonStatusRepository extends JpaRepository<PersonStatus, Integer> {
    @Override
    <S extends PersonStatus> S save(S entity);

    @Modifying
    @Query(
            value = "update person_status set isFound=b'1', removedAt=?2 where person_id=?1",
            nativeQuery = true)
    void markPersonAsFound(long personId, LocalDateTime foundAt);
}
