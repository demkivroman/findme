package org.demkiv.domain.service;

import org.demkiv.web.model.form.EmailForm;

public interface SenderService {

    void sendEmail(EmailForm  emailForm);
    boolean emailConfirmation(String token);
}
