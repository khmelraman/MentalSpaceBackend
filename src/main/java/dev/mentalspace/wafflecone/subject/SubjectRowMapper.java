package dev.mentalspace.wafflecone.subject;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SubjectRowMapper implements RowMapper<Subject> {
    @Override
    public Subject mapRow(ResultSet row, int rowNum) throws SQLException {
        Subject subject = new Subject();
        subject.subjectId = row.getLong("subject_id");
        subject.department = row.getString("department");
        subject.description = row.getString("description");
        subject.name = row.getString("name");
        return subject;
    }
}
