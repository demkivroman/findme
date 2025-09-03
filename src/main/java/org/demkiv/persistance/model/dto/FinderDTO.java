package org.demkiv.persistance.model.dto;

import lombok.Builder;
import lombok.Data;
import org.demkiv.persistance.entity.SubscriptionStatus;

@Data
@Builder
public class FinderDTO {
    private String id;
    private String fullName;
    private boolean isPhoneProvided;
    private SubscriptionStatus emailStatus;
    private String information;
}
