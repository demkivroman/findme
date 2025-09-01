package org.demkiv.persistance.service;

import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;

import java.util.Map;

public interface ConverterService {
    PersonDTO convertQueryRowToPersonDTO(Map<String, Object> value);
    PhotoDTO convertQueryRowToPhotoDTO(Map<String, Object> value);
}
