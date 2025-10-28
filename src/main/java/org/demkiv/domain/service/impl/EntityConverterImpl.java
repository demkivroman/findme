package org.demkiv.domain.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.demkiv.domain.service.EntityConverter;
import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.entity.PersonStatus;
import org.demkiv.persistance.entity.Photo;
import org.demkiv.persistance.entity.Subscriptions;
import org.demkiv.persistance.model.dto.FinderDTO;
import org.demkiv.persistance.model.dto.PersonDTO;
import org.demkiv.persistance.model.dto.PhotoDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class EntityConverterImpl implements EntityConverter {

    @Override
    public PersonDTO convertToPersonDTO(Person person) {
        PersonStatus personStatus = person.getPersonStatus();
        String[] parts = splitDateTime(personStatus.getCreatedAt());

        return PersonDTO.builder()
                .id(person.getId())
                .fullName(person.getFullname())
                .birthday(formatBirthday(person.getBirthday()))
                .description(person.getDescription())
                .age(calculateAge(person.getBirthday()))
                .date(Objects.nonNull(parts) ? parts[0] : null)
                .time(Objects.nonNull(parts) ? parts[1]  : null)
                .build();
    }

    private String[] splitDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        String date = dateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String time = dateTime.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME);

        return new String[] { date, time };
    }


    private String formatBirthday(Date date) {
        if (date == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate localDate = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate.format(formatter);
    }

    private int calculateAge(Date birthDate) {
        if (birthDate == null) {
           return -1;
        }

        LocalDate birth = Instant.ofEpochMilli(birthDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        if (birth.isAfter(today)) {
            throw new IllegalArgumentException("birthDate is in the future");
        }

        return Period.between(birth, today).getYears();
    }

    @Override
    public FinderDTO convertToFinderDTO(Finder finder, Subscriptions emailStatus) {
        if (finder == null) {
            return null;
        }

        return FinderDTO.builder()
                .id(String.valueOf(finder.getId()))
                .fullName(finder.getFullname())
                .emailStatus(emailStatus != null ? emailStatus.getStatus() : null)
                .information(finder.getInformation())
                .isPhoneProvided(StringUtils.isNoneBlank(finder.getPhone()))
                .isEmailProvided(StringUtils.isNoneBlank(finder.getEmail()))
                .build();
    }

    @Override
    public List<PhotoDTO> convertToPhotoDTO(Set<Photo> photos) {
        return photos.stream()
                .map(photo -> PhotoDTO.builder()
                        .id(photo.getId())
                        .url(photo.getUrl())
                        .convertedUrl(photo.getConvertedUrl())
                        .build())
                .toList();
    }
}
