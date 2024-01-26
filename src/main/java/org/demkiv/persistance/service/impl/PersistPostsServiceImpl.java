package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PostsRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Posts;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.form.PostForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class PersistPostsServiceImpl implements SaveUpdateService<PostForm, Boolean> {
    private PostsRepository postsRepository;
    private PersonRepository personRepository;

    @Override
    public Boolean saveEntity(PostForm entity) {
        Posts posts = convertToPostsEntity(entity);
        postsRepository.save(posts);
        log.info("Post entity is saved to database id {}", posts.getId());
        return true;
    }

    @Override
    public Boolean updateEntity(PostForm entity) {
        Optional<Posts> postsOptional = postsRepository.findById(entity.getPostId());
        if (postsOptional.isPresent()) {
            Posts post = postsOptional.get();
            post.setPost(entity.getPost());
            post.setTime(getTimestamp());
            postsRepository.save(post);
            log.info("Post entity is updated in database id {}", post.getId());
            return true;
        }
        return false;
    }

    private Posts convertToPostsEntity(PostForm postForm) {
        Optional<Person> person = personRepository.findById(postForm.getPersonId());
        if (person.isEmpty()) {
            String message = String.format("Person with id %s is absent in database", postForm.getPersonId());
            log.error(message);
            throw new FindMeServiceException(message);
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
