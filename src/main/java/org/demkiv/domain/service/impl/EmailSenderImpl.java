package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.web.model.EmailModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service("emailSender")
public class EmailSenderImpl implements EntitySender<Boolean, EmailModel> {
    @Override
    public Boolean send(EmailModel entity) {
        log.info("Sending email {}", entity.getBody());
        return true;
    }
}
