package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.jdbc.core.JdbcTemplate;;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;


    public List<PersonFoundForm> findPersons(String fullName, String description) {
        String query = "select person.id, person.FULLNAME as person_fullname, person.BIRTHDAY, person.DESCRIPTION, finder.FULLNAME as finder_fullname, finder.PHONE, " +
                "finder.EMAIL, finder.INFORMATION, photo.URL from person\n" +
                "left join finder on person.FINDER_ID = finder.ID\n" +
                "left join photo on person.ID = photo.PERSON_ID\n" +
                "where person.FULLNAME like '%" + fullName + "%'" + " or " + "person.DESCRIPTION like '%" + description + "%'";

        List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query);
        return convertQueryResults(queryResult);
    }

    private List<PersonFoundForm> convertQueryResults(List<Map<String, Object>> queryResult) {
        Map<String, PersonFoundForm> foundPersonFormMap = new HashMap<>();
        queryResult.stream()
                .map(converter::convertPersonToPersonFoundForm)
                .forEach(personObject -> {
                    PersonFoundForm presentPersonInMap = foundPersonFormMap.get(personObject.getPersonId());
                    if (presentPersonInMap != null) {
                        List<String> tempUrls = new LinkedList<>();
                        tempUrls.addAll(presentPersonInMap.getUrls());
                        tempUrls.addAll(personObject.getUrls());
                        personObject.setUrls(tempUrls);
                    }
                    foundPersonFormMap.put(personObject.getPersonId(), personObject);
                });
        return foundPersonFormMap.values().stream()
                .collect(Collectors.toList());
    }

}
