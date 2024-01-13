package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonModel {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
    private PhotoModel photo;
}
