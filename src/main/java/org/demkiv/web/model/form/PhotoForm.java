package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoForm {
    private String id;
    private String url;
}
