package org.demkiv.persistance.model.response;

import lombok.Builder;
import lombok.Data;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;

import java.util.List;

@Data
@Builder
public class SearchPersonsModel {
    private PersonDTO person;
    private List<PhotoDTO> photo;
}
