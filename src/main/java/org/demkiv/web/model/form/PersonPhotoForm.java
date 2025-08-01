package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.nio.file.Path;

@ToString
@Data
@Builder
public class PersonPhotoForm {
    private long personId;
    private Path photoPath;
    private Path tempDirectory;
    private String url;
    private String thumbnailUrl;
}
