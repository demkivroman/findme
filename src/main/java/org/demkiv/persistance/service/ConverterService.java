package org.demkiv.persistance.service;

import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.web.model.FinderModel;
import org.demkiv.web.model.PersonModel;
import org.demkiv.web.model.PhotoModel;
import org.demkiv.web.model.PostModel;

import java.util.Map;

public interface ConverterService {
    PersonModel convertToPersonModel(Map<String, Object> value);
    FinderModel convertToFinderModel(Map<String, Object> value);
    PhotoModel convertToPhotoModel(Map<String, Object> value);
    PostModel convertToPostModel(Map<String, Object> value);
    PersonDTO convertQueryRowToPersonDTO(Map<String, Object> value);
    FinderDTO convertQueryRowToFinderDTO(Map<String, Object> value);
    PhotoDTO convertQueryRowToPhotoDTO(Map<String, Object> value);
}
