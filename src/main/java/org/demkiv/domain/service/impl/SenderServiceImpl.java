package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.domain.service.SenderService;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SenderServiceImpl implements SenderService {

    @Value("${emailFrom}")
    private String emailFrom;
    private final PersonRepository personRepository;
    @Qualifier("emailSender")
    private final EntitySender<Boolean, EmailModel> emailSender;

    @Override
    public void sendEmail(EmailForm emailForm) {
        long personId = Long.parseLong(emailForm.getPersonId());
        Person foundPerson = personRepository.findById(personId).get();
        String finderEmail = foundPerson.getFinder().getEmail();
        String subject;
        if (emailForm.getSendMode().equalsIgnoreCase("email")) {
            subject = String.format("Email from Findme site on [%s]", foundPerson.getFullname());
        } else if (emailForm.getSendMode().equalsIgnoreCase("sms")) {
            subject = String.format("SMS from Findme site on [%s]", foundPerson.getFullname());
        } else {
            subject = "Email from Findme site.";
        }
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(emailFrom)
                .emailTo(finderEmail)
                .body(emailForm.getBody())
                .subject(subject)
                .build();
        emailSender.send(emailModel);
        log.info("Email sent to {}. For person {}", finderEmail, foundPerson.getFullname());
    }
}
