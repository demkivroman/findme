package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.web.model.PersonForm;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "/api")
@AllArgsConstructor
public class PersonSupplier {
    private EntitySaver<PersonForm, Boolean> saver;

    @PostMapping(value = "/person/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePerson(

    ) {
        // saver.saveEntity(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .build();
    }
}
