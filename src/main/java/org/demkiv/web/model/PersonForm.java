package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class PersonForm {
    private String finderFullName;
    private String finderPhone;
    private String finderEmail;
    private String finderInformation;
    private String personFullName;
    private String personBirthDay;
    private String personDescription;
    private MultipartFile photo;
}
