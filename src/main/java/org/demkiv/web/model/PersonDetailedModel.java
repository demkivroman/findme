package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonDetailedModel<T> {
    private T person;
}
