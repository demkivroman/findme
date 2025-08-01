package org.demkiv.domain.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.persistance.dao.PersonRepository;
import org.demkiv.persistance.dao.PhotoRepository;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
public class PhotoServiceImpl implements EntitySaver<PersonPhotoForm, Boolean> {
    private final FileUploader<S3UploaderModel> s3Uploader;
    private final PersistService<PersonPhotoForm, Boolean> persistThumbnailService;
    private final PhotoRepository photoRepository;
    private final PersonRepository personRepository;
    private final ProcessRunner processRunner;
    private final Config config;

    @Autowired
    public PhotoServiceImpl(
            @Qualifier("s3Uploader") FileUploader<S3UploaderModel> s3Uploader,
            @Qualifier("thumbnailService") PersistService<PersonPhotoForm, Boolean> persistThumbnailService,
            PhotoRepository photoRepository,
            PersonRepository personRepository,
            ProcessRunner processRunner,
            Config config) {
        this.s3Uploader = s3Uploader;
        this.persistThumbnailService = persistThumbnailService;
        this.photoRepository = photoRepository;
        this.personRepository = personRepository;
        this.processRunner = processRunner;
        this.config = config;
    }

    @Override
    @Transactional
    public Boolean saveEntity(PersonPhotoForm personPhotoForm) {
        try {
            log.info("Start uploading person photo prcess");
            Path photoPath = personPhotoForm.getPhotoPath();
            File tempDir = personPhotoForm.getTempDirectory().toFile();
            String convertedPhotoPath = getConvertedPhotoPath(photoPath);
            String convertPhotoCommand = String.format(config.getConvertPhotoCommand(), photoPath, convertedPhotoPath);
            processRunner.runProcess(tempDir, convertPhotoCommand, new StringWriter());
            File photoInTempDir = new File(convertedPhotoPath);
            String thumbnailPath = getThumbnailPath(photoPath);
            String convertThumbnailCommand = String.format(config.getConvertThumbnailCommand(), photoPath, thumbnailPath);
            processRunner.runProcess(tempDir, convertThumbnailCommand, new StringWriter());
            File thumbnailInTempDir = new File(thumbnailPath);
            S3UploaderModel s3ThumbnailsModel = S3UploaderModel.builder()
                    .directory("thumbnails")
                    .file(thumbnailInTempDir)
                    .build();
            s3Uploader.upload(s3ThumbnailsModel);
            S3UploaderModel s3PhotosModel = S3UploaderModel.builder()
                    .directory("photos")
                    .file(photoInTempDir)
                    .build();
            s3Uploader.upload(s3PhotosModel);
            personPhotoForm.setUrl(String.format(config.getPhotosStoreUrl(), photoInTempDir.getName()));
            personPhotoForm.setThumbnailUrl(String.format(config.getThumbnailStoreUrl(), thumbnailInTempDir.getName()));
            savePhotoToDB(personPhotoForm);
            persistThumbnailService.saveEntity(personPhotoForm);
            return true;
        } catch (Throwable ex) {
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

    private String getThumbnailPath(Path path) {
        String photoPath = path.toFile().getAbsolutePath();
        String dirPath = photoPath.substring(0, photoPath.lastIndexOf(File.separator));
        String fileName = photoPath.substring(photoPath.lastIndexOf(File.separator) + 1);
        String[] arr = fileName.split("\\.");
        String thumbnailName = arr[0] + "_thumbnail.gif";
        return dirPath + File.separator + thumbnailName;
    }
}
