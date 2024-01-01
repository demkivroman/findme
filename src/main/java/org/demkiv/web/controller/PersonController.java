package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.SavePersonService;
import org.demkiv.web.model.PersonForm;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class PersonController {
    private final SavePersonService saver;

    @PostMapping(value = "/api/person/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePerson(
            @RequestParam("finder_fullname") String finderFullName,
            @RequestParam("finder_phone") String finderPhone,
            @RequestParam("finder_email") String finderEmail,
            @RequestParam("finder_information") String finderInformation,
            @RequestParam("person_fullname") String personFullName,
            @RequestParam("person_birthday") String personBirthDay,
            @RequestParam("person_description") String personDescription,
            @RequestParam("photo") MultipartFile photo
    ) {
        PersonForm personForm = PersonForm.builder()
                .finderFullName(finderFullName)
                .finderPhone(finderPhone)
                .finderEmail(finderEmail)
                .finderInformation(finderInformation)
                .personFullName(personFullName)
                .personBirthDay(personBirthDay)
                .personDescription(personDescription)
                .photo(photo)
                .build();

        saver.saveEntity(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .build();
    }
}
