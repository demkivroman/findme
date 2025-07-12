package org.demkiv.domain.upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.ConfigFile;
import org.demkiv.domain.architecture.FileUploader;
import org.demkiv.domain.model.S3UploaderModel;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component("s3Uploader")
public class S3Uploader implements FileUploader<S3UploaderModel> {
    private final ConfigFile config;

    @Override
    public void upload(S3UploaderModel model) {
        try {
            String key = String.format("%s/%s", model.getDirectory(), model.getFile().getName());
            AmazonS3 client = getAmazonS3Client();
            client.putObject(config.getS3BucketName(), key, model.getFile());
            log.info("Uploaded file to S3 {}", key);
        } catch (Exception ex) {
            log.error("File is not uploaded to S3.", ex);
            throw ex;
        }
    }

    private AmazonS3 getAmazonS3Client() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(config.getS3Region())
                .build();
    }
}
