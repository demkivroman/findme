package org.demkiv.persistance.service;

import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.dto.PostDTO;

import java.util.Map;

public interface ConverterService {
    PersonDTO convertQueryRowToPersonDTO(Map<String, Object> value);
    FinderDTO convertQueryRowToFinderDTO(Map<String, Object> value);
    PhotoDTO convertQueryRowToPhotoDTO(Map<String, Object> value);
    PostDTO convertQueryRowToPostDTO(Map<String, Object> value);
}
