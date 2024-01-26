package org.demkiv.persistance.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.persistance.dao.FinderRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.form.PersonForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service("persistPerson")
@AllArgsConstructor
@Transactional
public class PersistPersonServiceImpl implements SaveUpdateService<PersonForm, Optional<?>> {
    private FinderRepository finderRepository;
    private PersonRepository personRepository;

    @Override
    public Optional<Long> saveEntity(PersonForm personForm) {
        if (Objects.nonNull(personForm)) {
            Finder finder = getFinder(personForm);
            finderRepository.save(finder);
            log.info("Finder is stored to database {}", finder);
            Person person = getPerson(personForm, finder);
            Person savedPerson = personRepository.save(person);
            log.info("Person is stored to database {}", savedPerson);
            return Optional.of(savedPerson.getId());
        }
        throw new FindMeServiceException("Trying save empty person.");
    }

    @Override
    public Optional<Boolean> updateEntity(PersonForm entity) {
        Optional<Person> foundPerson = personRepository.findById(entity.getPersonId());
        if (foundPerson.isEmpty()) {
            throw new FindMeServiceException(String.format("Person with id - %s is not present in DB.", entity.getPersonId()));
        }
        Person person = foundPerson.get();
        Finder finder = person.getFinder();
        finder.setFullname(entity.getFinderFullName());
        finder.setPhone(entity.getFinderPhone());
        finder.setEmail(entity.getFinderEmail());
        finder.setInformation(entity.getFinderInformation());
        Finder updatedFinder = finderRepository.save(finder);
        log.info("Finder is updated in database {}", updatedFinder.getId());

        person.setFullname(entity.getPersonFullName());
        person.setDescription(entity.getPersonDescription());
        String stringDate = entity.getPersonBirthDay();
        if (!stringDate.equals(String.valueOf(person.getBirthday()))) {
            person.setBirthday(getDate(stringDate));
        }
        Person updatedPerson = personRepository.save(person);
        log.info("Person is updated in database {}", updatedPerson.getId());
        return Optional.of(true);
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
        Date birthDay;
        birthDay = getDate(stringDate);
        return Person.builder()
                .fullname(personForm.getPersonFullName())
                .birthday(birthDay)
                .description(personForm.getPersonDescription())
                .time(LocalDateTime.now())
                .finder(finder)
                .build();
    }

    private Date getDate(String dateString) {
        if (dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("d-MMM-yyyy");
        LocalDate birthDay = LocalDate.parse(dateString, df);
        Instant instant = Instant.from(birthDay.atStartOfDay(ZoneId.of("GMT")));
        return Date.from(instant);
    }
}
