package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.persistance.dao.FinderRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.PersonForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service("persistPerson")
@AllArgsConstructor
@Transactional
public class PersistPersonServiceImpl implements PersistService<PersonForm, Long> {
    private FinderRepository finderRepository;
    private PersonRepository personRepository;

    @Override
    public Long saveEntity(PersonForm personForm) {
        if (Objects.nonNull(personForm)) {
            Finder finder = getFinder(personForm);
            finderRepository.save(finder);
            log.info("Finder is stored to database {}", finder);
            Person person = getPerson(personForm, finder);
            Person savedPerson = personRepository.save(person);
            log.info("Person is stored to database {}", savedPerson);
            return savedPerson.getId();
        }
        throw new RuntimeException("Trying save empty person.");
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
