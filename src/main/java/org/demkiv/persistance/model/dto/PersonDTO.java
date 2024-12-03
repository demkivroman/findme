package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDTO {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
    private int age;
    private String date;
    private String time;
}
