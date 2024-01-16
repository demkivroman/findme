package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.impl.PersistPostsService;
import org.demkiv.web.model.form.PostForm;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsServiceImpl implements EntitySaver<PostForm, Boolean>, EntityFinder<String, List<?>> {
    private final PersistPostsService postsService;
    private final QueryRepository queryService;

    @Override
    public Boolean saveEntity(PostForm entity) {
        postsService.saveEntity(entity);
        log.info("Post [ {} ] is saved to database for person {}", entity.getPost(), entity.getPersonId());
        return true;
    }

    @Override
    public List<?> findEntity(String personId) {
        List<?> posts = queryService.getPersonPosts(personId);
        log.info("Found {} posts for person ID = {}", posts.size(), personId);
        return posts;
    }
}
