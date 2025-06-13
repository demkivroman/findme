package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.web.model.EmailModel;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailSender")
@RequiredArgsConstructor
public class EmailSenderImpl implements EntitySender<Boolean, EmailModel> {

    private final JavaMailSender emailSender;

    @Override
    public Boolean send(EmailModel entity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(entity.getEmailFrom());
        message.setTo(entity.getEmailTo());
        message.setSubject(entity.getSubject());
        message.setText(entity.getBody());
        emailSender.send(message);
        log.info("Email sent successfully to {}", entity.getEmailTo());
        return true;
    }
}
