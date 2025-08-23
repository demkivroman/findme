package org.demkiv.web.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.service.SenderService;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SendController {
    private final SenderService senderService;
    private final Config config;

    @PostMapping(value = "/api/email/send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> sendEmail(@RequestBody EmailForm emailForm) {
        senderService.sendEmail(emailForm);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/api/subscription/confirm")
    public void confirmSubscription(@RequestParam String token, HttpServletResponse response) throws IOException {
        boolean isConfirmed = senderService.emailConfirmation(token);
        if (isConfirmed) {
            log.info("Confirmed subscription");
            response.sendRedirect(config.getFrontDomain()+ "/confirmation-success");
        } else {
            log.error("Subscription confirmation failed");
            response.sendRedirect(config.getFrontDomain() + "/confirmation-failure");
        }
    }
}
