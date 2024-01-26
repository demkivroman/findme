package org.demkiv.persistance.model.response;

import lombok.Builder;
import lombok.Data;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;

import java.util.Set;

@Data
@Builder
public class PersonDetailModel {
    private PersonDTO person;
    private FinderDTO finder;
    private Set<PhotoDTO> photos;
    private String totalPosts;
}
