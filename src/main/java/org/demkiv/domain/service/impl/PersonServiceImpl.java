package org.demkiv.domain.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntityPersist;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.domain.service.PersonService;
import org.demkiv.persistance.dao.QueryRepository;
import org.demkiv.persistance.service.SaveUpdateService;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.ValidateCaptchaForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class PersonServiceImpl implements EntityPersist<PersonForm, Optional<?>>, PersonService {
    private final SaveUpdateService<PersonForm, Optional<?>> service;
    private final QueryRepository queryRepository;
    private final EntitySender<Boolean, EmailModel> emailSender;

    @Autowired
    public PersonServiceImpl(
            @Qualifier("persistPerson") SaveUpdateService<PersonForm, Optional<?>> service,
            QueryRepository queryRepository,
            @Qualifier("emailSender") EntitySender<Boolean, EmailModel> emailSender) {
        this.service = service;
        this.queryRepository = queryRepository;
        this.emailSender = emailSender;
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
        PersonResponseModel<?> foundInfo = queryRepository.getDetailedPersonInfoFromDB(personId);
        log.info("Detailed person information retrieved from DB. ID - {}", personId);
        return foundInfo;
    }

    @Override
    @Transactional
    public List<?> getRandomPersons(int count) {
        List<Long> ids = queryRepository.getPersonIds();
        Set<Long> idSetForCount = randomIds(ids, count);
        return queryRepository.getPersonsDataAndThumbnails(idSetForCount);
    }

    @Override
    public boolean generateCaptchaAndPushToSessionAndSendEmail(Long personId, HttpServletRequest request) {
        String captcha = generateCaptcha();

        HttpSession session = request.getSession();
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> capchaMap = oMapper.convertValue(session.getAttribute("captcha"), Map.class);
        if (capchaMap == null) {
            session.setAttribute("captcha", Map.of((String.valueOf(personId)), captcha));
        } else {
            capchaMap.put(String.valueOf(personId), captcha);
            session.setAttribute("captcha", capchaMap);
        }

        EmailModel emailModel = EmailModel.builder()
                .body(captcha)
                .build();
        return emailSender.send(emailModel);
    }

    @Override
    public boolean getCaptchaFromSessionAndValidate(ValidateCaptchaForm captchaForm, HttpServletRequest request) {
        HttpSession session = request.getSession();
        ObjectMapper oMapper = new ObjectMapper();
        Map<String, Object> capchaMap = oMapper.convertValue(session.getAttribute("captcha"), Map.class);

        if (capchaMap == null) {
            return false;
        }

        String captcha = capchaMap.get(captchaForm.getPersonId()).toString();
        log.info("Captcha retrieved from Session. PERSON_ID - {}, Captcha - {}", captchaForm.getPersonId(), captcha);

        return captcha.equals(captchaForm.getCaptcha());
    }

    private String generateCaptcha() {
        final int captchaLength = 10;
        final char[] numericAlphabet = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ@#&!".toCharArray();
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
