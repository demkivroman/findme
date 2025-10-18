package org.demkiv.domain.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.web.model.EmailModel;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailSender")
@RequiredArgsConstructor
public class EmailSenderImpl implements EntitySender<Boolean, EmailModel> {

    private final JavaMailSender emailSender;

    @Override
    public Boolean send(EmailModel entity) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(entity.getEmailFrom());
            helper.setTo(entity.getEmailTo());
            helper.setSubject(entity.getSubject());
            helper.setText(entity.getBody(), true);
            emailSender.send(message);
            log.info("Email sent successfully to {}", entity.getEmailTo());
            return true;
        } catch (MessagingException e) {
            throw new FindMeServiceException("Email sending failed", e);
        }
    }
}
