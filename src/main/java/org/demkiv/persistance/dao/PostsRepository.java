package org.demkiv.persistance.dao;

import org.demkiv.persistance.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    @Override
    <S extends Posts> S save(S entity);

    @Override
    Optional<Posts> findById(Long aLong);
}
