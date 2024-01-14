package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.persistance.model.response.PostModel;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Objects;

@Service
public class ConverterServiceImpl implements ConverterService {
    @Override
    public PersonModel convertToPersonModel(Map<String, Object> value) {
        String fullName = Objects.toString(value.get("person_fullname"));
        String birthDay = Objects.toString(value.get("BIRTHDAY"));
        String description = Objects.toString(value.get("DESCRIPTION"));
        return PersonModel.builder()
                .id(Objects.toString(value.get("person_id")))
                .fullName(fullName.equals("null") ? "" : fullName)
                .birthday(birthDay.equals("null") ? "" : birthDay)
                .description(description.equals("null") ? "" : description)
                .build();
    }

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
    public FinderModel convertToFinderModel(Map<String, Object> value) {
        return FinderModel.builder()
                .id(Objects.toString(value.get("finder_id")))
                .email(Objects.toString(value.get("email")))
                .fullName(Objects.toString(value.get("finder_fullname")))
                .phone(Objects.toString(value.get("phone")))
                .information(Objects.toString(value.get("information")))
                .build();
    }

    @Override
    public PhotoModel convertToPhotoModel(Map<String, Object> value) {
        return PhotoModel.builder()
                .id(Objects.toString(value.get("photo_id")))
                .url(Objects.toString(value.get("url")))
                .build();
    }

    @Override
    public PostModel convertToPostModel(Map<String, Object> value) {
        String id = Objects.toString(value.get("post_id"));
        if (id.equals("null") || id.isEmpty()) {
            return null;
        }

        String timestamp = Objects.toString(value.get("time"));
        String[] dateTimeArr = !timestamp.isEmpty() ? timestamp.split(" ") : new String[]{};
        return PostModel.builder()
                .id(id)
                .post(Objects.toString(value.get("post")))
                .date((dateTimeArr.length > 1)? dateTimeArr[0] : "")
                .time((dateTimeArr.length > 1)? dateTimeArr[1] : "")
                .build();
    }

    private String getCorrectFieldValue(Map<String, Object> row, String field) {
        String retrievedValue = Objects.toString(row.get(field));
        return retrievedValue.equals("null") ? "" : retrievedValue;
    }
}
