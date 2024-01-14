package org.demkiv.persistance.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostModel {
    String id;
    String post;
    String date;
    String time;
}
