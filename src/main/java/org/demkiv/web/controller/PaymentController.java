package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.impl.PersonPaymentImpl;
import org.demkiv.web.model.ResponseModel;
import org.demkiv.web.model.form.PaymentForm;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.demkiv.web.ResponseStatus.SUCCESS;

@RestController
@AllArgsConstructor
public class PaymentController {

    private final PersonPaymentImpl paymentService;

    @PostMapping(value = "/api/person/payment",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Boolean> personPaymentEvent(@RequestBody PaymentForm paymentForm) {
        boolean isSaved = paymentService.saveEntity(paymentForm);
        return ResponseModel.<Boolean>builder()
                .mode(SUCCESS.name())
                .body(isSaved)
                .build();
    }
}
