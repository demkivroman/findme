package org.demkiv.web.model.form;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostForm {
    private String post;
    private long personId;
}
