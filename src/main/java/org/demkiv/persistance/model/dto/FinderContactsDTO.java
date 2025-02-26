package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinderContactsDTO {
    private String phone;
    private String email;
}
