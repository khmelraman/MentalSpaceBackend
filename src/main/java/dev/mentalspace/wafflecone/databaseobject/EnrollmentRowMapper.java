package dev.mentalspace.wafflecone.databaseobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EnrollmentRowMapper implements RowMapper<Enrollment> {
    @Override
    public Enrollment mapRow(ResultSet row, int rowNum) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.enrollmentId = row.getLong("enrollment_id");
        enrollment.studentId = row.getLong("student_id");
        enrollment.periodId = row.getLong("period_id");
        enrollment.studentPreference = row.getInt("student_preference");
        return enrollment;
    }
}
