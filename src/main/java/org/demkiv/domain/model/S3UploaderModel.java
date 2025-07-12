package org.demkiv.domain.model;

import lombok.Builder;
import lombok.Data;

import java.io.File;

@Data
@Builder
public class S3UploaderModel {

    private File file;
    private String directory;
}
