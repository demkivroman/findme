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
        String birthday = convertDateTimeToArray(getCorrectFieldValue(row, "birthday"))[0];
        String[] dateTimeArr = convertDateTimeToArray(getCorrectFieldValue(row, "time"));
        ZonedDateTime today = ZonedDateTime.now(ZoneId.of("UTC"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate birthDay = LocalDate.parse(birthday, df);
        ZonedDateTime zoned = birthDay.atStartOfDay(ZoneId.of("UTC"));
        int age = today.getYear() - zoned.getYear();

        return PersonDTO.builder()
                .id(getCorrectFieldValue(row, "person_id"))
                .fullName(getCorrectFieldValue(row, "person_fullname"))
                .birthday(birthday)
                .description(getCorrectFieldValue(row, "description"))
                .age(age)
                .date(dateTimeArr[0])
                .time(dateTimeArr[1])
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
        String[] dateTimeArr = convertDateTimeToArray(getCorrectFieldValue(row, "time"));
        return PostDTO.builder()
                .id(getCorrectFieldValue(row, "id"))
                .post(getCorrectFieldValue(row, "post"))
                .date(dateTimeArr[0])
                .time(dateTimeArr[1])
                .build();
    }

    private String[] convertDateTimeToArray(String timestamp) {
        return timestamp.isEmpty() ? new String[]{"",""} : timestamp.split("(\\s)|(T)");
    }

    private String getCorrectFieldValue(Map<String, Object> row, String field) {
        String retrievedValue = Objects.toString(row.get(field));
        return retrievedValue.equals("null") || retrievedValue.isEmpty() ? "" : retrievedValue;
    }
}
