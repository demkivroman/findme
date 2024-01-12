package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostModel {
    String id;
    String post;
    String timestamp;
}
