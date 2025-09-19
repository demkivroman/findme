package org.demkiv.web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.service.impl.PhotoServiceImpl;
import org.demkiv.domain.service.impl.PersonServiceImpl;
import org.demkiv.domain.util.TempDirectory;
import org.demkiv.web.model.PersonResponseModel;
import org.demkiv.web.model.ResponseModel;
import org.demkiv.web.model.form.PersonForm;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.demkiv.web.model.form.ValidateCaptchaForm;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
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

        try(TempDirectory tempDirectory = new TempDirectory("temp_photo")) {
            Path photoPath = getTempPhotoPath(tempDirectory.getPath(), photo);
            PersonPhotoForm photoForm = PersonPhotoForm.builder()
                    .personId(personId)
                    .photoPath(photoPath)
                    .tempDirectory(tempDirectory.getPath())
                    .build();

            log.info("Processing save for person ID {}", personId);
            photoService.addPhoto(photoForm);

            return ResponseModel.builder()
                    .mode("Success")
                    .build();
        } catch (IOException e) {
            log.error("Failed to save person photo in controller [/api/person/save/photo] for person ID {}", personId, e);
            throw new RuntimeException(e);
        }
    }

    private Path getTempPhotoPath(Path tempDirectory, MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            log.error("Uploaded photo is null or empty");
            throw new FindMeServiceException("Uploaded photo is empty");
        }

        long size = photo.getSize();
        if (size == 0) {
            log.error("Uploaded photo has size zero");
            throw new FindMeServiceException("Uploaded photo has size zero");
        }

        // Acquire InputStream safely
        try (InputStream in = photo.getInputStream()) {
            log.debug("After retrieving input stream.");

            String fileName = convertToCorrectPhotoName(Objects.requireNonNull(photo.getOriginalFilename()));
            log.debug("File name is [{}]", fileName);

            File image = new File(tempDirectory.toFile(), fileName);
            log.debug("Image path is [{}]", image.getAbsolutePath());

            // Save the uploaded file to the temp location
            Files.copy(in, Path.of(image.toURI()), StandardCopyOption.REPLACE_EXISTING);

            log.info("Temp image file is created {}", image.getPath());
            return image.toPath();
        } catch (IOException e) {
            log.error("Failed to read input stream or save file", e);
            throw new FindMeServiceException("Failed to process uploaded image");
        }
    }

    private String convertToCorrectPhotoName(String photoName) {
        String fileNameSuffix = photoName.substring(photoName.lastIndexOf(".") + 1);
        String fileNamePrefix = photoName.substring(0, photoName.lastIndexOf("."));
        return fileNamePrefix.replaceAll("(\\s+)|(-+)", "_") + "." + fileNameSuffix;
    }


    @PostMapping(value = "/api/person/delete/photo/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Boolean> deletePersonPhoto(String id) {
        photoService.deletePhoto(id);
        return ResponseModel.<Boolean>builder()
                .mode("Success")
                .build();
    }

    @GetMapping(value = "/api/person/images/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<List<?>> getImagesForPerson(@PathVariable String id) {
        return ResponseModel.<List<?>>builder()
                .mode("Success")
//                .body(personService.getPhotoUrlsFromDBForPerson(id))
                .body(List.of())
                .build();
    }

    @GetMapping(value = "/api/person/information/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> getDetailedPersonInfo(@PathVariable String id) {
        PersonResponseModel<?> result = personService.getDetailedPersonInfo(id);
        return ResponseModel.builder()
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
    public ResponseModel<String> createCaptchaMessage(@PathVariable long personId) {
        String captchaKey = personService.processCaptchaCreation(personId);
        return ResponseModel.<String>builder()
                .mode("Success")
                .body(captchaKey)
                .build();
    }

    @PostMapping(value = "/api/captcha/validate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<Boolean> validateCaptchaMessage(@RequestBody ValidateCaptchaForm captchaForm) {
        boolean result = personService.checkCaptcha(captchaForm);
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
