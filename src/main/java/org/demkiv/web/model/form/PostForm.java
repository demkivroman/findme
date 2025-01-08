package org.demkiv.web.model.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostForm {
    private long postId;
    private long personId;
    private String author;
    private String post;
}
