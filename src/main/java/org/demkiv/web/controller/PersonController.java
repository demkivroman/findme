package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.impl.PhotoServiceImpl;
import org.demkiv.domain.service.impl.PersonServiceImpl;
import org.demkiv.web.model.*;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class PersonController {
    private final PersonServiceImpl personService;
    private final PhotoServiceImpl photoService;

    @PostMapping(value = "/api/person/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePerson(@RequestBody PersonForm personForm) {
        Optional<?> result = personService.saveEntity(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .body(String.valueOf(result))
                .build();
    }

    @PostMapping(value = "/api/person/{id}/update",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> updatePerson(
            @PathVariable String id,
            @RequestBody PersonForm personForm) {
        personForm.setPersonId(Long.parseLong(id));
        Optional<?> result = personService.updateEntity(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .body(result)
                .build();
    }

    @PostMapping(value = "/api/person/save/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePersonPhoto(
            @RequestParam("person_id") long personId,
            @RequestParam("photo") MultipartFile photo) {
        PersonPhotoForm photoForm = PersonPhotoForm.builder()
                .personId(personId)
                .photo(photo)
                .build();
        photoService.saveEntity(photoForm);

        return ResponseModel.builder()
                .mode("Success")
                .build();
    }

    @GetMapping(value = "/api/person/information/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<PersonResponseModel<?>> getDetailedPersonInfo(@PathVariable String id) {
        PersonResponseModel<?> result = personService.getDetailedPersonInfo(id);
        return ResponseModel.<PersonResponseModel<?>>builder()
                .mode("Success")
                .body(result)
                .build();
    }
}
