package org.demkiv.persistance.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Posts extends JpaRepository<Posts, Long> {
    @Override
    <S extends Posts> S save(S entity);
    @Override
    Optional<Posts> findById(Long aLong);
}
