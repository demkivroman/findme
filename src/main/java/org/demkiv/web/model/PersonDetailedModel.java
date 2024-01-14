package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class PersonDetailedModel <T,K,E> {
    private T person;
    private K finder;
    private Set<E> photos;
    private String totalPosts;
}
