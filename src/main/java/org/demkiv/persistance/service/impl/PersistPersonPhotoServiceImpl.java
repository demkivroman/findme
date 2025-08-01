package org.demkiv.persistance.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PhotoRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("photoService")
@AllArgsConstructor
@Transactional
public class PersistPersonPhotoServiceImpl implements PersistService<PersonPhotoForm, Boolean> {
    private PhotoRepository photoRepository;
    private PersonRepository personRepository;

    @Override
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        log.info("saveEntity() was called for personId={}", personPhotoForm.getPersonId());
        Optional<Person> personEntity = personRepository.findById(personPhotoForm.getPersonId());
        if (personEntity.isEmpty()) {
            log.error("Can't find person in database by id " + personPhotoForm.getPersonId());
            throw new FindMeServiceException("Can't find person in database by id " + personPhotoForm.getPersonId());
        }

        Photo photoEntity = getPhoto(personEntity.get(), personPhotoForm.getUrl());
        photoRepository.save(photoEntity);
        log.info("Photo is saved to database. URL is {}", personPhotoForm.getUrl());
        return true;
    }

    private Photo getPhoto(Person person, String url) {
        return Photo.builder()
                .url(url)
                .person(person)
                .build();
    }
}
