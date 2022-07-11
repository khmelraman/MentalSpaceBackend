package dev.mentalspace.wafflecone.school;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SchoolRowMapper implements RowMapper<School> {
    @Override
    public School mapRow(ResultSet row, int rowNum) throws SQLException {
        School school = new School();
        school.schoolId = row.getLong("school_id");
        school.shortName = row.getString("short_name");
        school.name = row.getString("name");
        school.address = row.getString("address");
        return school;
    }
}
