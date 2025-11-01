package org.demkiv.domain.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.demkiv.domain.Config;
import org.demkiv.domain.FindMeServiceException;
import org.demkiv.domain.architecture.EntitySaver;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.ImageConverter;
import org.demkiv.domain.service.PhotoService;
import org.demkiv.domain.service.S3Service;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PhotoServiceImpl implements PhotoService {

    private static final String CONVERTED_PHOTO_DIRECTORY = "converted";
    private final S3Service s3Service;
    private final PhotoRepository photoRepository;
    private final PersonRepository personRepository;
    private final ImageConverter imageConverter;
    private final Config config;

    @Override
    public void addPhoto(PersonPhotoForm personPhotoForm) {
        try {
            log.info("Start uploading person photo process");
            Path photoPath = personPhotoForm.getPhotoPath();
            File convertedPhoto = convertPhotoToSquare(photoPath);
            String photoKey = Optional.of(photoPath)
                            .map(Path::getFileName)
                            .map(name -> config.getBucketPhotoDirectory() + "/" + name)
                            .orElse("");
            s3Service.upload(photoKey, Objects.requireNonNull(photoPath).toFile());
            String convertedPhotoKey = Optional.of(convertedPhoto)
                            .map(File::getName)
                            .map(name -> config.getBucketPhotoDirectory() + "/" + CONVERTED_PHOTO_DIRECTORY + "/" + name)
                            .orElse("");
            s3Service.upload(convertedPhotoKey, convertedPhoto);
            savePhotoToDB(personPhotoForm.getPersonId(), photoKey, convertedPhotoKey);
        } catch (Exception ex) {
            log.error("Error when storing an image. " + ex.getMessage());
            throw new FindMeServiceException("Error when storing an image. " + ex.getMessage(), ex);
        }
    }

    private File convertPhotoToSquare(Path photoOrigin) throws IOException {
        String  photoPath = getConvertedPhotoPath(photoOrigin);
        byte[] originalBytes = Files.readAllBytes(photoOrigin);
        byte[] convertedImage = imageConverter.resizeWithPadding(originalBytes, 350);
        try (FileOutputStream fos = new FileOutputStream(photoPath)) {
            fos.write(convertedImage);
            return new File(photoPath);
        }
    }

    private void savePhotoToDB(long personId, String photoKey, String convertedPhotoKey) throws IOException {
        Optional<Person> personEntity = personRepository.findById(personId);
        if (personEntity.isEmpty()) {
            log.error("Can't find person in database by id " + personId);
            throw new FindMeServiceException("Can't find person in database by id " + personId);
        }

        Photo photoEntity = getPhoto(personEntity.get(), photoKey, convertedPhotoKey);
        photoRepository.save(photoEntity);
        log.info("Photo is saved to database. URLS is {}, {}", photoKey, convertedPhotoKey);
    }

    private Photo getPhoto(Person person, String photoKey, String convertedPhotoKey) {
        return Photo.builder()
                .url(String.format(config.getPhotosStoreUrl(), photoKey))
                .convertedUrl(String.format(config.getPhotoStoreConvertedUrl(), convertedPhotoKey))
                .person(person)
                .build();
    }

    private String getConvertedPhotoPath(Path path) {
        return Optional.of(path)
                .map(Path::toString)
                .map(name -> name.replaceAll("(\\.\\w+)$", ""))
                .map(name -> name + "_converted.jpg")
                .orElse("");
    }

    @Override
    public void deletePhoto(String id) {
        Optional<Photo> foundPhoto = photoRepository.findById(Long.parseLong(id));
        if (foundPhoto.isEmpty()) {
            log.error("Can't find person in database by id " + id);
            return;
        }
        Photo photo = foundPhoto.get();
        s3Service.deletePhotoFromS3(photo.getUrl());
        photoRepository.delete(photo);
        log.info("Photo deleted from database. ID is {}", id);
    }
}
