package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PersonPhotoForm {
    private long personId;
    private MultipartFile photo;
}
