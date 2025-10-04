package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityFinder;
import org.demkiv.domain.architecture.EntityPersist;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PostsRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Posts;
import org.demkiv.persistance.model.dto.PostDTO;
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
    private final PostsRepository postsRepository;
    private final PersonRepository personRepository;

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
        Optional<Person> foundPerson = personRepository.findById(Long.parseLong(personId));
        if (foundPerson.isEmpty()) {
            log.error("Person [ {} ] not found in database. When getting posts", personId);
            return List.of();
        }

        List<Posts> posts = postsRepository.findAllByPerson(foundPerson.get());
        log.info("Found {} posts for person ID = {}", posts.size(), personId);

        return posts.stream()
                .map(post -> PostDTO.builder()
                        .id(String.valueOf(post.getId()))
                        .post(post.getPost())
                        .author(post.getAuthor())
                        .timestamp(post.getTime())
                        .build()
                )
                .toList();
    }
}
