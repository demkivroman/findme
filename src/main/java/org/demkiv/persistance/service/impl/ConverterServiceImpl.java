package org.demkiv.persistance.service.impl;

import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.demkiv.persistance.model.dto.PostDTO;
import org.demkiv.persistance.service.ConverterService;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class ConverterServiceImpl implements ConverterService {

    @Override
    public PersonDTO convertQueryRowToPersonDTO(Map<String, Object> row) {
        String[] birthday = convertDateTimeToArray(getCorrectFieldValue(row, "birthday"));
        int age = 0;
        String[] dateTimeArr = convertDateTimeToArray(getCorrectFieldValue(row, "time"));
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");
        if (birthday.length > 0) {
            LocalDate birthDay = LocalDate.parse(birthday[0], df);
            ZonedDateTime zoned = birthDay.atStartOfDay(ZoneId.of("UTC"));
            age = today.getYear() - zoned.getYear();
        }
        String time = dateTimeArr[1].substring(0, dateTimeArr[1].lastIndexOf("."));

        return PersonDTO.builder()
                .id(getCorrectFieldValue(row, "person_id"))
                .fullName(getCorrectFieldValue(row, "person_fullname"))
                .birthday(birthday.length > 0 ? birthday[0] : null)
                .description(getCorrectFieldValue(row, "description"))
                .age(birthday.length > 0 ? age : -1)
                .date(dateTimeArr[0])
                .time(time)
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
        String photoUrl = getCorrectFieldValue(row, "photo_url");
        String photoId = getCorrectFieldValue(row, "photo_id");
        if (photoUrl.isEmpty() && photoId.isEmpty()) {
            return null;
        }

        return PhotoDTO.builder()
                .id(photoId)
                .url(photoUrl)
                .build();
    }

    @Override
    public PostDTO convertQueryRowToPostDTO(Map<String, Object> row) {
        String[] dateTimeArr = convertDateTimeToArray(getCorrectFieldValue(row, "time"));
        return PostDTO.builder()
                .id(getCorrectFieldValue(row, "id"))
                .post(getCorrectFieldValue(row, "post"))
                .date(dateTimeArr[0])
                .time(dateTimeArr[1])
                .build();
    }

    private String[] convertDateTimeToArray(String timestamp) {
        return timestamp.isEmpty() ? new String[]{} : timestamp.split("(\\s)|(T)");
    }

    private String getCorrectFieldValue(Map<String, Object> row, String field) {
        String retrievedValue = Objects.toString(row.get(field));
        return retrievedValue.equals("null") || retrievedValue.isEmpty() ? "" : retrievedValue;
    }
}
