package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FinderModel {
    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String information;
}
