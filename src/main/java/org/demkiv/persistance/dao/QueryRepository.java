package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonModel;
import org.demkiv.web.model.PhotoModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;


    public List<PersonModel> findPersonsAndPhoto(String fullName, String description) {
        String query = "select person.id as person_id, person.FULLNAME, person.BIRTHDAY, person.DESCRIPTION, "  +
                "photo.id as photo_id, photo.URL from person\n" +
                "left join photo on person.ID = photo.PERSON_ID\n" +
                "where person.FULLNAME like '%" + fullName + "%'" + " or " + "person.DESCRIPTION like '%" + description + "%'";

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    private List<PersonModel> convertQueryResults(List<Map<String, Object>> queryResult) {
        Map<String, PersonModel> persons = new HashMap<>();
        queryResult.forEach(line -> {
            collectPerson(persons, line);
        });

        return new ArrayList<>(persons.values());
    }

    private void collectPerson(Map<String, PersonModel> persons, Map<String, Object> queryRow) {
        PersonModel currentPerson = converter.convertToPersonModel(queryRow);
        PhotoModel photo = converter.convertToPhotoModel(queryRow);
        currentPerson.setPhoto(photo);
        persons.putIfAbsent(currentPerson.getId(), currentPerson);
    }
}
