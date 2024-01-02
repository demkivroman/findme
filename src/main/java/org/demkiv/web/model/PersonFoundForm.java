package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class PersonFoundForm {
    private String id;
    private String personFullName;
    private Date personBirthday;
    private String personDescription;
    private List<String> urls;
    private String finderFullName;
    private String finderPhone;
    private String finderEmail;
    private String finderInformation;
}
