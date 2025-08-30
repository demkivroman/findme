package org.demkiv.persistance.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.persistance.dao.FinderRepository;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PersonStatusRepository;
import org.demkiv.persistance.dao.SubscriptionsRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.PersonStatus;
import org.demkiv.persistance.entity.SubscriptionStatus;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.PersonForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service("persistPerson")
@RequiredArgsConstructor
@Transactional
public class PersistPersonServiceImpl implements SaveUpdateService<PersonForm, Optional<?>> {

    private static final String SUBSCRIPTION_TEXT = """
            <h4>Email confirmation from FindMe resource</h4>
            <p>Please confirm your subscription by clicking the link below:</p>
            <a href="%s">Confirm Subscription</a>
            """;

    private final Config config;
    private final FinderRepository finderRepository;
    private final PersonRepository personRepository;
    private final PersonStatusRepository personStatusRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    @Qualifier("emailSender")
    private final EntitySender<Boolean, EmailModel>  emailSender;

    @Override
    public Optional<Long> saveEntity(PersonForm personForm) {
        if (Objects.isNull(personForm)) {
            Finder finder = getFinder(personForm);
            Finder savedFinder = null;
            if (Objects.nonNull(finder)) {
                savedFinder = finderRepository.save(finder);
                log.info("Finder is stored to database {}", finder);
            }
            Person person = getPerson(personForm, finder);
            Person savedPerson = personRepository.save(person);
            log.info("Person is stored to database {}", savedPerson);
            PersonStatus personStatus = getPersonStatus(person);
            PersonStatus savedPersonStatus = personStatusRepository.save(personStatus);
            log.info("PersonStatus is stored to database {}", savedPersonStatus);
            if (Objects.nonNull(finder)) {
                sendSubscriptionNotification(savedFinder);
            }
            return Optional.of(savedPerson.getId());
        }
        log.error("Trying save empty person.");
        throw new FindMeServiceException("Trying save empty person.");
    }

    private void sendSubscriptionNotification(Finder savedFinder) {
        String email = savedFinder.getEmail();

        if (StringUtils.isEmpty(email)) {
            log.warn("Email subscription is not sent, because email is not provided.");
            return;
        }

        Optional<Subscriptions> foundSubscription = subscriptionsRepository.findByEmail(email);
        Subscriptions subscription = foundSubscription.orElse(null);
        if (foundSubscription.isPresent() && subscription.getStatus() == SubscriptionStatus.CONFIRMED) {
            return;
        }

        String token = UUID.randomUUID().toString();
        String confirmationLink = config.getDomain() + "/findme/api/subscription/confirm?token=" + token;
        String emailBody = String.format(SUBSCRIPTION_TEXT, confirmationLink);
        sendConfirmationMail(emailBody, email);
        if (foundSubscription.isEmpty()) {
            storeSubscriptionToDB(token, email);
        } else {
            subscription.setToken(token);
            subscription.setEmail(email);
            subscriptionsRepository.save(subscription);
            log.info("Subscription is updated in database {}", subscription);
        }
    }

    private void storeSubscriptionToDB(String token, String email) {
        Subscriptions subscriptions = Subscriptions.builder()
                .token(token)
                .email(email)
                .status(SubscriptionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        subscriptionsRepository.save(subscriptions);
        log.info("Subscription has been stored to database {}", subscriptions);
    }



    private void sendConfirmationMail(String emailBody, String email) {
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(config.getEmailFrom())
                .emailTo(email)
                .subject("Email confirmation from FindMe resource")
                .body(emailBody)
                .build();
        emailSender.send(emailModel);
    }

    @Override
    public Optional<Boolean> updateEntity(PersonForm personForm) {
        Optional<Finder> foundFinder = finderRepository.findById(Long.parseLong(personForm.getFinderId()));
        if (foundFinder.isEmpty()) {
            log.error("Finder with id - {} is not present in DB.", personForm.getFinderId());
            throw new FindMeServiceException(String.format("Finder with id - %s is not present in DB.", personForm.getFinderId()));
        }

        Finder finder = foundFinder.get();
        finder.setFullname(personForm.getFinderFullName());
        finder.setPhone(personForm.getFinderPhone());
        finder.setEmail(personForm.getFinderEmail());
        finder.setInformation(personForm.getFinderInformation());
        Finder updatedFinder = finderRepository.save(finder);
        log.info("Finder is updated in database {}", updatedFinder.getId());

        Optional<Person> foundPerson = personRepository.findById(Long.parseLong(personForm.getPersonId()));
        if (foundPerson.isEmpty()) {
            log.error(String.format("Person with id - %s is not present in DB.", personForm.getPersonId()));
            throw new FindMeServiceException(String.format("Person with id - %s is not present in DB.", personForm.getPersonId()));
        }
        Person person = foundPerson.get();
        person.setFullname(personForm.getPersonFullName());
        person.setDescription(personForm.getPersonDescription());
        String stringDate = personForm.getPersonBirthDay();
        if (!stringDate.equals(String.valueOf(person.getBirthday()))) {
            person.setBirthday(getDate(stringDate));
        }
        Person updatedPerson = personRepository.save(person);
        log.info("Person is updated in database {}", updatedPerson.getId());
        return Optional.of(true);
    }

    private PersonStatus getPersonStatus(Person person) {
        return PersonStatus.builder()
                .isFound(false)
                .createdAt(LocalDateTime.now())
                .person(person)
                .build();
    }

    private Finder getFinder(PersonForm personForm) {
        if (isFinderEmpty(personForm)) {
            return null;
        }
        
        if (StringUtils.isNotEmpty(personForm.getFinderPhone())) {
            Optional<Finder> foundFinder = finderRepository.findByPhone(personForm.getFinderPhone());
            if (foundFinder.isPresent()) {
                Finder finder = foundFinder.get();
                updateFinderInfo(personForm, finder);
                return finder;
            }
        }

        if (StringUtils.isNotEmpty(personForm.getFinderEmail())) {
            Optional<Finder> foundFinderByEmail = finderRepository.findByEmail(personForm.getFinderEmail());
            if (foundFinderByEmail.isPresent()) {
                Finder finder = foundFinderByEmail.get();
                updateFinderInfo(personForm, finder);
                return finder;
            }
        }
        return Finder.builder()
                .fullname(personForm.getFinderFullName())
                .email(personForm.getFinderEmail())
                .phone(personForm.getFinderPhone())
                .information(personForm.getFinderInformation())
                .build();
    }
    
    private boolean isFinderEmpty(PersonForm personForm) {
        return StringUtils.isBlank(personForm.getFinderFullName()) &&
                StringUtils.isBlank(personForm.getFinderEmail()) &&
                StringUtils.isBlank(personForm.getFinderInformation()) &&
                StringUtils.isBlank(personForm.getFinderPhone()) &&
                StringUtils.isBlank(personForm.getFinderId());
    }

    private void updateFinderInfo(PersonForm personForm, Finder finder) {
        if (StringUtils.isNotEmpty(personForm.getFinderFullName()) && !personForm.getFinderFullName().equals(finder.getFullname())) {
            finder.setFullname(personForm.getFinderFullName());
        }

        if (StringUtils.isNotEmpty(personForm.getFinderPhone()) && !personForm.getFinderPhone().equals(finder.getPhone())) {
            finder.setPhone(personForm.getFinderPhone());
        }

        if (StringUtils.isNotEmpty(personForm.getFinderEmail()) && !personForm.getFinderEmail().equals(finder.getEmail())) {
            finder.setEmail(personForm.getFinderEmail());
        }

        if (StringUtils.isNotEmpty(personForm.getFinderInformation()) && !personForm.getFinderInformation().equals(finder.getInformation())) {
            finder.setInformation(personForm.getFinderInformation());
        }
    }

    private Person getPerson(PersonForm personForm, Finder finder) {
        String stringDate = personForm.getPersonBirthDay();
        Date birthDay;
        birthDay = getDate(stringDate);
        return Person.builder()
                .finder(finder)
                .fullname(personForm.getPersonFullName())
                .birthday(birthDay)
                .description(personForm.getPersonDescription())
                .finder(finder)
                .build();
    }

    private Date getDate(String dateString) {
        if (dateString.isEmpty()) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDay = LocalDate.parse(dateString, df);
        Instant instant = Instant.from(birthDay.atStartOfDay(ZoneId.of("GMT")));
        return Date.from(instant);
    }
}
