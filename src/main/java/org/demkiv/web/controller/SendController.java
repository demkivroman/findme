package org.demkiv.web.controller;

import lombok.RequiredArgsConstructor;
import org.demkiv.domain.service.SenderService;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SendController {
    private final SenderService senderService;

    @PostMapping(value = "/api/email/send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> sendEmail(@RequestBody EmailForm emailForm) {
        senderService.sendEmail(emailForm);
        return ResponseEntity.ok().body(true);
    }
}
