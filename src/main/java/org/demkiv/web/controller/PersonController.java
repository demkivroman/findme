package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.PersonPhotoService;
import org.demkiv.domain.service.PersonService;
import org.demkiv.web.model.PersonForm;
import org.demkiv.web.model.PersonPhotoForm;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.ResponseModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class PersonController {
    private final PersonService saver;
    private final PersonPhotoService photoService;

    @PostMapping(value = "/api/person/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePerson(@RequestBody PersonForm personForm) {
        long result = saver.saveEntity(personForm);
        return ResponseModel.builder()
                .mode("Success")
                .body(PersonResponseModel
                        .builder()
                        .personId(String.valueOf(result))
                        .build())
                .build();
    }

    @PostMapping(value = "/api/person/save/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePersonPhoto(
            @RequestParam("person_id") long personId,
            @RequestParam("photo") MultipartFile photo
    ) {
        PersonPhotoForm photoForm = PersonPhotoForm.builder()
                .personId(personId)
                .photo(photo)
                .build();
        photoService.saveEntity(photoForm);

        return ResponseModel.builder()
                .mode("Success")
                .build();
    }
}
