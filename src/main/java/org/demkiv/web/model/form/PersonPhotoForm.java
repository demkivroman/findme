package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;
import org.demkiv.domain.util.TempDirectory;

import java.nio.file.Path;

@Data
@Builder
public class PersonPhotoForm {
    private long personId;
    private Path photoPath;
    private TempDirectory tempDirectory;
    private String url;
    private String thumbnailUrl;
}
