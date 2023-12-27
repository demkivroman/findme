package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Override
    <S extends Photo> S save(S entity);
    @Override
    Optional<Photo> findById(Long aLong);
}
