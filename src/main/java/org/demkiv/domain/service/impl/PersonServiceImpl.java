package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntityPersist;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.domain.service.EntityConverter;
import org.demkiv.domain.service.PersonService;
import org.demkiv.domain.util.CaptchaCache;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PersonStatusRepository;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.dao.SubscriptionsRepository;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.response.PersonDetailModel;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.ValidateCaptchaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PersonServiceImpl implements EntityPersist<PersonForm, Optional<?>>, PersonService {

    @Value("${emailFrom}")
    private String emailFrom;
    @Value("${captcha.ttl}")
    private long captchaTtl;

    private final SaveUpdateService<PersonForm, Optional<?>> service;
    private final QueryRepository queryRepository;
    private final EntitySender<Boolean, EmailModel> emailSender;
    private final PersonStatusRepository personStatusRepository;
    private final PersonRepository personRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final EntityConverter converter;
    private final CaptchaCache captchaCache;

    @Autowired
    public PersonServiceImpl(
            @Qualifier("persistPerson") SaveUpdateService<PersonForm, Optional<?>> service,
            QueryRepository queryRepository,
            @Qualifier("emailSender") EntitySender<Boolean, EmailModel> emailSender,
            PersonStatusRepository personStatusRepository,
            PersonRepository personRepository,
            SubscriptionsRepository subscriptionsRepository,
            EntityConverter converter) {
        this.service = service;
        this.queryRepository = queryRepository;
        this.emailSender = emailSender;
        this.personStatusRepository = personStatusRepository;
        this.personRepository = personRepository;
        this.subscriptionsRepository = subscriptionsRepository;
        this.converter = converter;
        this.captchaCache = CaptchaCache.getInstance();
    }

    @Override
    public Optional<?> saveEntity(PersonForm entity) {
        return service.saveEntity(entity);
    }

    @Override
    public Optional<?> updateEntity(PersonForm entity) {
        return service.updateEntity(entity);
    }

    @Override
    @Transactional
    public PersonResponseModel<?> getDetailedPersonInfo(String personId) {
        Optional<Person> foundPerson = personRepository.findById(Long.parseLong(personId));
        if (foundPerson.isEmpty()) {
            throw new FindMeServiceException("Person with id " + personId + " not found");
        }
        Person person = foundPerson.get();
        Finder finder = person.getFinder();
        Set<Photo> photos = person.getPhotos();
        String email = finder != null ? finder.getEmail() : null;
        Optional<Subscriptions> emailStatus = subscriptionsRepository.findByEmail(email);
        PersonDTO personDTO = converter.convertToPersonDTO(person);
        FinderDTO finderDTO = converter.convertToFinderDTO(finder,  Objects.nonNull(finder) ? emailStatus.get() : null);
        List<PhotoDTO> photoDTO = converter.convertToPhotoDTO(photos);
        PersonDetailModel personDetailModel = PersonDetailModel.builder()
                .person(personDTO)
                .photos(photoDTO)
                .finder(finderDTO)
                .totalPosts(person.getPosts().size())
                .build();
        log.info("Detailed person information retrieved from DB. ID - {}", personId);
        return PersonResponseModel.builder()
                .person(personDetailModel)
                .build();
    }

    @Override
    @Transactional
    public List<?> getRandomPersons(int count) {
        List<Long> ids = queryRepository.getPersonIds();
        Set<Long> idSetForCount = randomIds(ids, count);
        return queryRepository.getPersonsDataAndThumbnails(idSetForCount);
    }

    @Override
    public String processCaptchaCreation(long personId) {
        String captcha = generateCaptcha();
        String key = UUID.randomUUID().toString();
        captchaCache.put(key, captcha, captchaTtl);
        Person foundPerson = personRepository.findById(personId).get();
        String finderEmail = foundPerson.getFinder().getEmail();
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(emailFrom)
                .emailTo(finderEmail)
                .body(String.format("Please use this code for %s. Captcha - %s", foundPerson.getFullname(), captcha))
                .subject(String.format("Captcha code for %s", foundPerson.getFullname()))
                .build();
        emailSender.send(emailModel);
        return key;
    }

    @Override
    public boolean checkCaptcha(ValidateCaptchaForm captchaForm) {
        String captcha = captchaCache.get(captchaForm.getCaptchaId());
        log.debug("Captcha Cache {}", captchaCache);
        return captchaForm.getCaptcha().equals(captcha);
    }

    @Override
    public String generateSessionId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    @Override
    @Transactional
    public boolean markPersonAsFound(long personId) {
        personStatusRepository.markPersonAsFound(personId, LocalDateTime.now());
        log.info("Marked person as found in DB. ID - {}", personId);
        return true;
    }

    protected String generateCaptcha() {
        final int captchaLength = 10;
        final char[] numericAlphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-".toCharArray();
        StringBuilder captchaBuilder = new StringBuilder();

        for (int i = 0; i < captchaLength; i++) {
            int randomIndex = getRandomDiceNumber(numericAlphabet.length - 1);
            captchaBuilder.append(numericAlphabet[randomIndex]);
        }

        return captchaBuilder.toString();
    }

    private Set<Long> randomIds(List<Long> ids, int count) {
        Set<Long> personIds = new HashSet<>();

        while (personIds.size() < count && personIds.size() < ids.size()) {
            int randomIndex = getRandomDiceNumber(ids.size());
            personIds.add(ids.get(randomIndex));
        }

        return personIds;
    }

    public int getRandomDiceNumber(int count)
    {
        return (int) (Math.random() * count);
    }
}
