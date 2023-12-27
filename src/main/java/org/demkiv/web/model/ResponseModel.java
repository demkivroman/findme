package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseModel<T> {
    private String mode;
    private T body;
}
