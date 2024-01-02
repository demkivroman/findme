package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.persistance.entity.Person;
import org.demkiv.persistance.mapper.PersonRowMapper;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;

    private final static String PERSONS_SEARCH = "select * from person where FULLNAME like '%?%' or DESCRIPTION like '%?%'";

    public List<PersonFoundForm> findPersons(String fullName, String description) {
        List<Person> persons = Collections.singletonList(jdbcTemplate.queryForObject(PERSONS_SEARCH, new PersonRowMapper(), fullName, description));
        return persons.stream()
                .map(converter::convertPersonToPersonFoundForm)
                .collect(Collectors.toList());
    }

}
