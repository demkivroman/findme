package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
    @Override
    <S extends Thumbnail> S save(S entity);

    @Override
    Optional<Thumbnail> findById(Long aLong);
}
