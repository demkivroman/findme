package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.web.model.ResponseModel;
import org.demkiv.web.model.form.EmailForm;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class SendController {

    @PostMapping(value = "/api/email/send",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> sendEmail(@RequestBody EmailForm personForm) {
        System.out.println(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .build();
    }
}
