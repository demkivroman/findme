package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDTO {
    private String id;
    private String post;
    private String date;
    private String time;
}
