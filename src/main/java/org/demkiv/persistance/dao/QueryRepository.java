package org.demkiv.persistance.dao;

import lombok.RequiredArgsConstructor;
import org.demkiv.persistance.service.ConverterService;
import org.demkiv.web.model.PersonFoundForm;
import org.springframework.jdbc.core.JdbcTemplate;;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class QueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ConverterService converter;


    public List<PersonFoundForm> findPersons(String fullName, String description) {
        String sql = "select * from person where fullname like '%" + fullName + "%' or description like '%" + description + "%'";
        List<Map<String, Object>> w = jdbcTemplate.queryForList(sql);
        return null;
    }

}
