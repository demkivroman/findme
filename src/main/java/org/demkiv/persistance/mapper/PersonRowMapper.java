package org.demkiv.persistance.mapper;

import org.demkiv.persistance.entity.Finder;
import org.demkiv.persistance.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {
    @Override
    public Person mapRow(ResultSet resultSet, int i) throws SQLException {
        Person person = new Person();

        person.setId(resultSet.getLong("ID"));
        person.setFullname(resultSet.getString("fullname"));
        person.setBirthday(resultSet.getDate("birthday"));
        person.setDescription(resultSet.getString("description"));
        person.setFinder(resultSet.getObject("finder_id", Finder.class));
        return person;
    }
}
