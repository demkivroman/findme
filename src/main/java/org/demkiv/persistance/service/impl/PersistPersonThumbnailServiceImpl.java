package org.demkiv.persistance.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.ThumbnailRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Thumbnail;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("thumbnailService")
@AllArgsConstructor
@Transactional
public class PersistPersonThumbnailServiceImpl implements PersistService<PersonPhotoForm, Boolean> {
    private ThumbnailRepository thumbnailRepository;
    private PersonRepository personRepository;

    @Override
    public Boolean saveEntity(PersonPhotoForm entity) {
        Optional<Person> personEntity = personRepository.findById(entity.getPersonId());
        if (personEntity.isEmpty()) {
            log.error("Can't find person in database by id " + entity.getPersonId());
            throw new FindMeServiceException("Can't find person in database by id " + entity.getPersonId());
        }

        Thumbnail photoEntity = getPhoto(personEntity.get(), entity.getThumbnailUrl());
        thumbnailRepository.save(photoEntity);
        log.info("Thumbnail is saved to database. URL is {}", entity.getThumbnailUrl());
        return true;
    }

    private Thumbnail getPhoto(Person person, String url) {
        return Thumbnail.builder()
                .url(url)
                .person(person)
                .build();
    }
}
