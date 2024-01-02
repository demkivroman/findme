package org.demkiv.persistance.mapper;

import org.demkiv.persistance.entity.Finder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FinderRowMapper implements RowMapper<Finder> {
    @Override
    public Finder mapRow(ResultSet resultSet, int i) throws SQLException {
        Finder finder = new Finder();

        finder.setId(resultSet.getLong("ID"));
        finder.setFullname(resultSet.getString("fullname"));
        finder.setPhone(resultSet.getString("phone"));
        finder.setEmail(resultSet.getString("email"));
        finder.setInformation(resultSet.getString("information"));
        return finder;
    }
}
