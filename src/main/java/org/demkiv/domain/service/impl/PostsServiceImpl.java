package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.domain.architecture.EntityPersist;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.impl.PersistPostsServiceImpl;
import org.demkiv.web.model.form.PostForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostsServiceImpl implements EntityPersist<PostForm, Optional<?>>, EntityFinder<String, List<?>> {
    private final PersistPostsServiceImpl postsService;
    private final QueryRepository queryService;

    @Override
    public Optional<?> saveEntity(PostForm entity) {
        postsService.saveEntity(entity);
        log.info("Post [ {} ] is saved to database for person {}", entity.getPost(), entity.getPersonId());
        return Optional.of(true);
    }

    @Override
    public Optional<?> updateEntity(PostForm entity) {
        postsService.updateEntity(entity);
        log.info("Post [ {} ] is updated in database for person {}", entity.getPost(), entity.getPersonId());
        return Optional.of(true);
    }

    @Override
    public List<?> findEntity(String personId) {
        List<?> posts = queryService.getPersonPosts(personId);
        log.info("Found {} posts for person ID = {}", posts.size(), personId);
        return posts;
    }
}
