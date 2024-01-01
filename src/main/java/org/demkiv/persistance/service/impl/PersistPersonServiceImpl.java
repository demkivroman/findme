package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.persistance.dao.FinderRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PhotoRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class PersistPersonServiceImpl implements PersistService<PersonForm> {
    private FinderRepository finderRepository;
    private PersonRepository personRepository;
    private PhotoRepository photoRepository;

    @Override
    public void savePerson(PersonForm personForm, String photoUrl) {
        if (Objects.nonNull(personForm)) {
            Finder finder = getFinder(personForm);
            finderRepository.save(finder);
            log.info("Finder is stored to database {}", finder);
            Person person = getPerson(personForm, finder);
            personRepository.save(person);
            log.info("Person is stored to database {}", person);
            photoRepository.save(getPhoto(person, photoUrl));
            log.info("Photo linked to person is saved to database. URL is {}", photoUrl);
        }
    }

    private Photo getPhoto(Person person, String url) {
        return Photo.builder()
                .url(url)
                .person(person)
                .build();
    }

    private Finder getFinder(PersonForm personForm) {
        return Finder.builder()
                .fullname(personForm.getFinderFullName())
                .email(personForm.getFinderEmail())
                .phone(personForm.getFinderPhone())
                .information(personForm.getFinderInformation())
                .build();
    }

    private Person getPerson(PersonForm personForm, Finder finder) {
        String stringDate = personForm.getPersonBirthDay();
        Date birthDay = null;
        if (!stringDate.equals("")) {
            birthDay = getDate(stringDate);
        }
        return Person.builder()
                .fullname(personForm.getPersonFullName())
                .birthday(birthDay)
                .description(personForm.getPersonDescription())
                .finder(finder)
                .build();
    }

    private Date getDate(String dateString) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MMM-yyyy");
        LocalDate birthDay = LocalDate.parse(dateString, df);
        Instant instant = Instant.from(birthDay.atStartOfDay(ZoneId.of("GMT")));
        return Date.from(instant);
    }
}
