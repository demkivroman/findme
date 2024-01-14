package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersonResponseModel<T> {
    private T person;
}
