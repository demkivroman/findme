package org.demkiv.domain.upload;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demkiv.domain.ConfigFile;
import org.demkiv.domain.architecture.FileUploader;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@AllArgsConstructor
@Component("s3Uploader")
public class S3Uploader implements FileUploader<File> {
    private final ConfigFile configFile;

    @Override
    public void upload(File file) {
        String key = configFile.getS3ImageKey() + "/" + file.getName();
        String bucketName = configFile.getS3BucketName();
        AmazonS3 client = getAmazonS3Client();
        try {
            if (!fileExist(client, key ,bucketName)) {
                client.putObject(bucketName, key, file);
                log.info("{} image is uploaded.", key);
            } else {
                log.info("{} already exists on S3", key);
            }
        } catch (Exception ex) {
            log.error("{} image is not uploaded. {}", key, ex.getMessage());
            throw ex;
        }
    }

    private AmazonS3 getAmazonS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                configFile.getS3AccessKey(),
                configFile.getS3SecretKey()
        );

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    private boolean fileExist(AmazonS3 client, String key, String awsBucketName) {
            try {
                client.getObjectMetadata(awsBucketName, key);
            } catch (AmazonS3Exception ex) {
                return false;
            }
        return true;
    }
}
