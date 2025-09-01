package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoDTO {
    private Long id;
    private String url;
}
