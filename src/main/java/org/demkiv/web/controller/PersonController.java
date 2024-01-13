package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import org.demkiv.domain.service.impl.PhotoServiceImpl;
import org.demkiv.domain.service.impl.PersonServiceImpl;
import org.demkiv.web.model.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class PersonController {
    private final PersonServiceImpl personService;
    private final PhotoServiceImpl photoService;

    @PostMapping(value = "/api/person/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> savePerson(@RequestBody PersonForm personForm) {
        long result = personService.saveEntity(personForm);
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

    @GetMapping(value = "/api/person/information/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<PersonDetailedModel> getDetailedPersonInfo(@PathVariable String id) {
        PersonDetailedModel result = personService.getDetailedPersonInfo(id);
        return ResponseModel.<PersonDetailedModel>builder()
                .mode("Success")
                .body(result)
                .build();
    }
}
