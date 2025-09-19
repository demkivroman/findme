package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.PhotoService;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PhotoRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class PhotoServiceImpl implements PhotoService {
    private final FileUploader<S3UploaderModel> s3Uploader;
    private final PhotoRepository photoRepository;
    private final PersonRepository personRepository;
    private final Config config;

    @Autowired
    public PhotoServiceImpl(
            @Qualifier("s3Uploader") FileUploader<S3UploaderModel> s3Uploader,
            PhotoRepository photoRepository,
            PersonRepository personRepository,
            Config config) {
        this.s3Uploader = s3Uploader;
        this.photoRepository = photoRepository;
        this.personRepository = personRepository;
        this.config = config;
    }

    @Override
    public void addPhoto(PersonPhotoForm personPhotoForm) {
        try {
            log.info("Start uploading person photo process");
            Path photoPath = personPhotoForm.getPhotoPath();
            String convertedPhotoPath = getConvertedPhotoPath(photoPath);
            Files.copy(photoPath, Path.of(convertedPhotoPath), StandardCopyOption.REPLACE_EXISTING);
            File photoInTempDir = new File(convertedPhotoPath);
            S3UploaderModel s3PhotosModel = S3UploaderModel.builder()
                    .directory("photos")
                    .file(photoInTempDir)
                    .build();
            s3Uploader.upload(s3PhotosModel);
            personPhotoForm.setUrl(String.format(config.getPhotosStoreUrl(), photoInTempDir.getName()));
            savePhotoToDB(personPhotoForm);
        } catch (Exception ex) {
            log.error("Error when storing an image. " + ex.getMessage());
            throw new FindMeServiceException("Error when storing an image. " + ex.getMessage(), ex);
        }
    }

    private void savePhotoToDB(PersonPhotoForm personPhotoForm) {
        Optional<Person> personEntity = personRepository.findById(personPhotoForm.getPersonId());
        if (personEntity.isEmpty()) {
            log.error("Can't find person in database by id " + personPhotoForm.getPersonId());
            throw new FindMeServiceException("Can't find person in database by id " + personPhotoForm.getPersonId());
        }

        Photo photoEntity = getPhoto(personEntity.get(), personPhotoForm.getUrl());
        photoRepository.save(photoEntity);
        log.info("Photo is saved to database. URL is {}", personPhotoForm.getUrl());
    }

    private Photo getPhoto(Person person, String url) {
        return Photo.builder()
                .url(url)
                .person(person)
                .build();
    }

    private String getConvertedPhotoPath(Path path) {
        String photoPath = path.toString();
        String pathWithoutExtension = photoPath.substring(0, photoPath.lastIndexOf("."));
        return pathWithoutExtension  + "_converted.gif";
    }

    @Override
    public void deletePhoto(String id) {
        photoRepository.deleteById(Long.valueOf(id));
        log.info("Photo deleted from database. ID is {}", id);
    }
}
