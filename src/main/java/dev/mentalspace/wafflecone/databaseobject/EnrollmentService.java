package dev.mentalspace.wafflecone.databaseobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class EnrollmentService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
    public Enrollment getById(long id) {
        String sql = 
            "SELECT enrollment_id, student_id, period_id, student_preference " + 
            "FROM enrollment " + 
            "WHERE enrollment_id = ?;";
        RowMapper<Enrollment> rowMapper = new EnrollmentRowMapper();
        Enrollment enrollment = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return enrollment;
    }

    public void addEnrollment (Enrollment enrollment) {
        String sql = 
            "INSERT INTO enrollment (student_id, period_id, student_preference) " + 
            "(?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps =
						connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setLong      (1, enrollment.studentId);
					ps.setLong      (2, enrollment.periodId);
					ps.setInt       (3, enrollment.studentPreference);
					return ps;
                }
            }, 
            keyHolder
        );

        enrollment.enrollmentId = keyHolder.getKey().longValue();
    }

    public void updateEnrollment (Enrollment enrollment) {
        String sql = 
            "UPDATE enrollment SET " + 
            "student_id = ?, period_id = ?, student_preference = ? " + 
            "WHERE enrollment_id = ?;";
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = 
                        connection.prepareStatement(sql);
                    ps.setLong      (1, enrollment.studentId);
                    ps.setLong      (2, enrollment.periodId);
                    ps.setInt       (3, enrollment.studentPreference);
                    ps.setLong      (4, enrollment.enrollmentId);
                    return ps;
                }
            }
        );
    }

    public void deleteEnrollment (Enrollment enrollment) {
        String sql = 
            "DELETE FROM enrollment " + 
            "WHERE enrollment_id = ?;";
        jdbcTemplate.update(
            new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps =
                        connection.prepareStatement(sql);
                    ps.setLong(1, enrollment.enrollmentId);
                    return ps;
                }
            }
        );
    }
}