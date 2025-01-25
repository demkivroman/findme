package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailModel {
    private String emailFrom;
    private String emailTo;
    private String subject;
    private String body;
}
