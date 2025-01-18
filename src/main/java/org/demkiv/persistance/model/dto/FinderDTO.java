package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinderDTO {
    private String id;
    private String fullName;
    private boolean isPhoneProvided;
    private boolean isEmailProvided;
    private String information;
}
