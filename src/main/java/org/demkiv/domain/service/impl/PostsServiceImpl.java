package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.persistance.service.impl.PersistPostsService;
import org.demkiv.web.model.PostForm;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostsServiceImpl implements EntitySaver<PostForm, Boolean> {
    private final PersistPostsService postsService;

    @Override
    public Boolean saveEntity(PostForm entity) {
        postsService.saveEntity(entity);
        log.info("Post [ {} ] is saved to database for person {}", entity.getPost(), entity.getPersonId());
        return true;
    }
}
