package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PhotoModel {
    private String id;
    private String url;
}
