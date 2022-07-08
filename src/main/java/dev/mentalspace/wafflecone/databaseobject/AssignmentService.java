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
public class AssignmentService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Assignment getById(long id) {
        String sql = "SELECT assignment_id, period_id, date_assigned, date_due, type, estimated_burden, name, description "
                + "FROM assignment WHERE assignment_id = ?;";
        RowMapper<Assignment> rowMapper = new AssignmentRowMapper();
        Assignment assignment = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return assignment;
    }

    public List<Assignment> getByPeriodId(long id) {
        String sql = "SELECT assignment_id, period_id, date_assigned, date_due, type, estimated_burden, name, description "
                + "FROM assignment WHERE period_id = ?;";
        RowMapper<Assignment> rowMapper = new AssignmentRowMapper();
        List<Assignment> assignments = jdbcTemplate.query(sql, rowMapper, id);
        return assignments;
    }

    public void addAssignment(Assignment assignment) {
        String sql = "INSERT INTO assignment (period_id, date_assigned, date_due, type, estimated_burden, name, description) VALUES "
                + "(?, ?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, assignment.periodId);
                ps.setLong(2, assignment.dateAssigned);
                ps.setLong(3, assignment.dateDue);
                ps.setString(4, assignment.type);
                ps.setLong(5, assignment.estimatedBurden);
                ps.setString(6, assignment.name);
                ps.setString(7, assignment.description);
                return ps;
            }
        }, keyHolder);

        assignment.assignmentId = keyHolder.getKey().longValue();
    }

    public void updateAssignment(Assignment assignment) {
        String sql = "UPDATE assignment SET "
                + "period_id = ?, date_assigned = ?, date_due = ?, type = ?, estimated_burden = ?, name = ?, description = ? "
                + "WHERE assignment_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, assignment.periodId);
                ps.setLong(2, assignment.dateAssigned);
                ps.setLong(3, assignment.dateDue);
                ps.setString(4, assignment.type);
                ps.setLong(5, assignment.estimatedBurden);
                ps.setString(6, assignment.name);
                ps.setString(7, assignment.description);
                ps.setLong(8, assignment.assignmentId);
                return ps;
            }
        });
    }

    public void deleteAssignment(Assignment assignment) {
        String sql = "DELETE FROM assignment WHERE assignment_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, assignment.assignmentId);
                return ps;
            }
        });
    }
}