package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.dto.PostDTO;
import org.demkiv.persistance.service.ConverterService;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

@Service
public class ConverterServiceImpl implements ConverterService {

    @Override
    public PersonDTO convertQueryRowToPersonDTO(Map<String, Object> row) {
        return PersonDTO.builder()
                .id(getCorrectFieldValue(row, "person_id"))
                .fullName(getCorrectFieldValue(row, "person_fullname"))
                .birthday(getCorrectFieldValue(row, "birthday"))
                .description(getCorrectFieldValue(row, "description"))
                .build();
    }

    @Override
    public FinderDTO convertQueryRowToFinderDTO(Map<String, Object> row) {
        return FinderDTO.builder()
                .id(getCorrectFieldValue(row, "finder_id"))
                .fullName(getCorrectFieldValue(row, "finder_fullname"))
                .email(getCorrectFieldValue(row, "email"))
                .phone(getCorrectFieldValue(row, "phone"))
                .information(getCorrectFieldValue(row, "information"))
                .build();
    }

    @Override
    public PhotoDTO convertQueryRowToPhotoDTO(Map<String, Object> row) {
        return PhotoDTO.builder()
                .id(getCorrectFieldValue(row, "photo_id"))
                .url(getCorrectFieldValue(row, "url"))
                .build();
    }

    @Override
    public PostDTO convertQueryRowToPostDTO(Map<String, Object> row) {
        String timestamp = getCorrectFieldValue(row, "time");
        String[] dateTimeArr = timestamp.isEmpty() ? null : timestamp.split(" ");
        return PostDTO.builder()
                .id(getCorrectFieldValue(row, "id"))
                .post(getCorrectFieldValue(row, "post"))
                .date(dateTimeArr == null ? "" : dateTimeArr[0])
                .time(dateTimeArr == null ? "" : dateTimeArr[1])
                .build();
    }

    private String getCorrectFieldValue(Map<String, Object> row, String field) {
        String retrievedValue = Objects.toString(row.get(field));
        return retrievedValue.equals("null") || retrievedValue.isEmpty() ? "" : retrievedValue;
    }
}
