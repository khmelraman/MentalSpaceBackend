package dev.mentalspace.wafflecone.student;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class StudentRowMapper implements RowMapper<Student> {
    @Override
    public Student mapRow(ResultSet row, int rowNum) throws SQLException {
        Student student = new Student();
        student.studentId = row.getLong("student_id");
        student.firstName = row.getString("first_name");
        student.canonicalId = row.getString("canonical_id");
        student.lastName = row.getString("last_name");
        student.phone = row.getLong("phone");
        student.grade = row.getInt("grade");
        return student;
    }
}
