package org.demkiv.persistance.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDTO {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
}
