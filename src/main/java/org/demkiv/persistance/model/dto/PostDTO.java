package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDTO implements Comparable<PostDTO> {
    private String id;
    private String author;
    private String post;
    private LocalDateTime timestamp;

    @Override
    public int compareTo(PostDTO o) {
        return timestamp.compareTo(o.getTimestamp());
    }
}
