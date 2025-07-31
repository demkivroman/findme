package org.demkiv.domain;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.demkiv.domain.service.ProcessRunner;
import org.demkiv.domain.util.TempDirectory;
import org.demkiv.persistance.service.PersistService;
import org.demkiv.web.model.form.PersonPhotoForm;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Path;

@Slf4j
@Setter
public class PersonUploadTask extends Thread {
    private FileUploader<S3UploaderModel> s3Uploader;
    private PersistService<PersonPhotoForm, Boolean> persistPhotoService;
    private PersistService<PersonPhotoForm, Boolean> persistThumbnailService;
    private ProcessRunner processRunner;
    private PersonPhotoForm personPhotoForm;
    private Config config;

    @Override
    public void run() {
        try {
            TempDirectory tempDirectory = personPhotoForm.getTempDirectory();
            log.info("Start uploading person photo prcess");
            Path photoPath = personPhotoForm.getPhotoPath();
            File tempDir = tempDirectory.getPath().toFile();
            String convertedPhotoPath = getConvertedPhotoPath(photoPath);
            String convertPhotoCommand = String.format(config.convertPhotoCommand, photoPath, convertedPhotoPath);
            processRunner.runProcess(tempDir, convertPhotoCommand, new StringWriter());
            File photoInTempDir = new File(convertedPhotoPath);
            String thumbnailPath = getThumbnailPath(photoPath);
            String convertThumbnailCommand = String.format(config.convertThumbnailCommand, photoPath, thumbnailPath);
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
            log.debug("After setting properties to personPhotoForm {}", personPhotoForm);
            persistPhotoService.saveEntity(personPhotoForm);
            persistThumbnailService.saveEntity(personPhotoForm);
        } catch (Throwable ex) {
            log.error("Error when storing an image. " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
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
