package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailForm {

    private String personId;
    private String sendMode;
    private String body;
}
