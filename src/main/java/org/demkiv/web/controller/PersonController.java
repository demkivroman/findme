package org.demkiv.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.demkiv.domain.service.impl.PhotoServiceImpl;
import org.demkiv.domain.service.impl.PersonServiceImpl;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.ResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.demkiv.web.model.form.PhotoForm;
import org.demkiv.web.model.form.ValidateCaptchaForm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
                .body(String.valueOf(result.get()))
                .build();
    }

    @PostMapping(value = "/api/person/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updatePerson(@RequestBody PersonForm personForm) {
        Optional<?> result = personService.updateEntity(personForm);
        if (result.isPresent()) {
            return ResponseEntity.ok("{\"updated\" : true}");
        }
        return ResponseEntity.ok("{\"updated\" : false}");
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

    @PostMapping(value = "/api/person/delete/photo",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Boolean> deletePersonPhoto(@RequestBody PhotoForm photoForm) {
        boolean result = personService.deletePhotoAndThumbnailFromDB(photoForm.getId(), photoForm.getUrl());
        return ResponseModel.<Boolean>builder()
                .mode("Success")
                .body(result)
                .build();
    }

    @GetMapping(value = "/api/person/images/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<?>> getImagesForPerson(@PathVariable String id) {
        return ResponseModel.<List<?>>builder()
                .mode("Success")
                .body(personService.getPhotoUrlsFromDBForPerson(id))
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

    @GetMapping(value = "/api/generate/session/id",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<String> generateSessionId() {
        return ResponseModel.<String>builder()
                .mode("Success")
                .body(personService.generateSessionId())
                .build();
    }

    @GetMapping(value = "/api/random/persons/{count}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<?> getRandomGeneratedPersons(@PathVariable int count) {
        return personService.getRandomPersons(count);
    }

    @GetMapping(value = "/api/captcha/create/{personId}")
    public ResponseModel<Boolean> createCaptchaMessage(@PathVariable long personId, HttpServletRequest request) {
        boolean result = personService.generateCaptchaAndPushToSessionAndSendEmail(personId, request);
        return ResponseModel.<Boolean>builder()
                .mode("Success")
                .body(result)
                .build();
    }

    @PostMapping(value = "/api/captcha/validate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Boolean> validateCaptchaMessage(@RequestBody ValidateCaptchaForm captchaForm, HttpServletRequest request) {
        boolean result = personService.getCaptchaFromSessionAndValidate(captchaForm, request);
        return ResponseModel.<Boolean>builder()
                .mode("Success")
                .body(result)
                .build();
    }

    @GetMapping(value = "/api/person/found/{personId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> markPersonAsFound(@PathVariable long personId) {
        personService.markPersonAsFound(personId);
        return ResponseEntity.ok("{\"updated\" : true}");
    }
}
