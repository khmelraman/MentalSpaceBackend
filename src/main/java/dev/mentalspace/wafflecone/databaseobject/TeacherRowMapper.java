package dev.mentalspace.wafflecone.databaseobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TeacherRowMapper implements RowMapper<Teacher> {
    @Override
    public Teacher mapRow(ResultSet row, int rowNum) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.teacherId    = row.getLong   ("teacher_id");
        teacher.canonicalId  = row.getString ("canonical_id");
        teacher.firstName    = row.getString ("first_name");
        teacher.lastName     = row.getString ("last_name");
        teacher.phone        = row.getLong   ("phone");
        teacher.department   = row.getString ("department");
        return teacher;
    }
}
