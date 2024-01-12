package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

@Data
@Builder
public class PersonModel {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
    private FinderModel finder;
    private Set<PhotoModel> urls;
    private Set<PostModel> posts;
}
