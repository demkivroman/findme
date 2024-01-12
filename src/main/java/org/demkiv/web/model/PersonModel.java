package org.demkiv.web.model;

import lombok.Builder;
import lombok.Data;
import org.demkiv.persistance.entity.Finder;

import java.util.List;

@Data
@Builder
public class PersonModel {
    private String id;
    private String fullName;
    private String birthday;
    private String description;
    private FinderModel finder;
    private List<PhotoModel> urls;
    private List<PostModel> posts;
}
