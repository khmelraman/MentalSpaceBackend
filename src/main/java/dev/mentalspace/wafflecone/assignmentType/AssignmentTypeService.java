package dev.mentalspace.wafflecone.assignmentType;

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
public class AssignmentTypeService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM assignment_type WHERE assignment_type_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
    }

    public AssignmentType getById(long id) {
        String sql = "SELECT assignment_type_id, teacher_id, value "
                + "FROM assignment_type WHERE assignment_type_id = ?;";
        RowMapper<AssignmentType> rowMapper = new AssignmentTypeRowMapper();
        AssignmentType assignmentEntryShortcut = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return assignmentEntryShortcut;
    }

    public List<AssignmentType> getByTeacherId(long id) {
        String sql = "SELECT assignment_type_id, teacher_id, value "
                + "FROM assignment_type WHERE teacher_id = ?;";
        RowMapper<AssignmentType> rowMapper = new AssignmentTypeRowMapper();
        List<AssignmentType> assignmentEntryShortcuts = jdbcTemplate.query(sql, rowMapper, id);
        return assignmentEntryShortcuts;
    }

    public void addAssignmentEntryShortcut(AssignmentType assignmentEntryShortcut) {
        String sql = "INSERT INTO assignment_type (teacher_id, value) VALUES " + 
                "(?,?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong  (1, assignmentEntryShortcut.teacherId);
                ps.setString(2, assignmentEntryShortcut.value);
                return ps;
            }
        }, keyHolder);

        assignmentEntryShortcut.assignmentEntryShortcutId = keyHolder.getKey().longValue();
    }

    public void updateAssignmentEntryShortcut(AssignmentType assignmentEntryShortcut) {
        String sql = "UPDATE assignment_type SET "
                + "teacher_id = ?, value = ? "
                + "WHERE assignment_type_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong  (1, assignmentEntryShortcut.teacherId);
                ps.setString(2, assignmentEntryShortcut.value);
                ps.setLong  (3, assignmentEntryShortcut.assignmentEntryShortcutId);
                return ps;
            }
        });
    }

    public void deleteAssignmentEntryShortcut(AssignmentType assignmentEntryShortcut) {
        String sql = "DELETE FROM assignment_type WHERE assignment_type_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, assignmentEntryShortcut.assignmentEntryShortcutId);
                return ps;
            }
        });
    }
}
