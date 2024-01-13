package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import org.demkiv.persistance.entity.Finder;

import java.util.Set;

@Data
@Builder
public class PersonDetailedModel {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
    private Finder finder;
    private Set<PhotoModel> photos;
    private String postsTotal;
}
