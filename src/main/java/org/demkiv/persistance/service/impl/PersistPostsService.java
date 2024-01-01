package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PostsRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Posts;
import org.demkiv.persistance.service.SaveEntityService;
import org.demkiv.web.model.PostForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class PersistPostsService implements SaveEntityService<PostForm> {
    private PostsRepository postsRepository;
    private PersonRepository personRepository;

    @Override
    public void saveEntity(PostForm entity) {
        Posts posts = convertToPostsEntity(entity);
        postsRepository.save(posts);
        log.info("Post entity is saved to database id {}", posts.getId());
    }

    private Posts convertToPostsEntity(PostForm postForm) {
        Optional<Person> person = personRepository.findById(postForm.getPersonId());
        if (person.isEmpty()) {
            String message = String.format("Person with id %s is absent in database", postForm.getPersonId());
            log.error(message);
            throw new RuntimeException(message);
        }
        return Posts.builder()
                .post(postForm.getPost())
                .person(person.get())
                .time(getTimestamp())
                .build();
    }

    private LocalDateTime getTimestamp() {
        return LocalDateTime.now();
    }
}
