package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.architecture.EntitySender;
import org.demkiv.web.model.EmailModel;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SendController {
    @Value("${emailFrom}")
    private final String mailFrom;
    private final EntitySender<Boolean, EmailModel> emailSender;

    @PostMapping(value = "/api/email/send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> sendEmail(@RequestBody EmailForm emailForm) {
        EmailModel emailModel = EmailModel.builder()
                .emailFrom(mailFrom)
                .emailTo(emailForm.getSendTo())
                .subject(emailForm.getSubject())
                .body(emailForm.getBody())
                .build();
        emailSender.send(emailModel);
        return ResponseEntity.ok().body(true);
    }
}
