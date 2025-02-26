package org.demkiv.web.model.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonForm {
    private String personId;
    private String personFullName;
    private String personBirthDay;
    private String personDescription;
    private String finderId;
    private String finderFullName;
    private String finderPhone;
    private String finderEmail;
    private String finderInformation;
}
