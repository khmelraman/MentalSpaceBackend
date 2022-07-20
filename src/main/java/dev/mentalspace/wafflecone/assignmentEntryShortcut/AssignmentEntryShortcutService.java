package dev.mentalspace.wafflecone.assignmentEntryShortcut;

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
public class AssignmentEntryShortcutService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM assignment_entry_shortcut WHERE assignment_entry_shortcut_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
    }

    public AssignmentEntryShortcut getById(long id) {
        String sql = "SELECT assignment_entry_shortcut_id, teacher_id, value "
                + "FROM assignment_entry_shortcut WHERE assignment_entry_shortcut_id = ?;";
        RowMapper<AssignmentEntryShortcut> rowMapper = new AssignmentEntryShortcutRowMapper();
        AssignmentEntryShortcut assignmentEntryShortcut = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return assignmentEntryShortcut;
    }

    public List<AssignmentEntryShortcut> getByTeacherId(long id) {
        String sql = "SELECT assignment_entry_shortcut_id, teacher_id, value "
                + "FROM assignment_entry_shortcut WHERE teacher_id = ?;";
        RowMapper<AssignmentEntryShortcut> rowMapper = new AssignmentEntryShortcutRowMapper();
        List<AssignmentEntryShortcut> assignmentEntryShortcuts = jdbcTemplate.query(sql, rowMapper, id);
        return assignmentEntryShortcuts;
    }

    public void addAssignmentEntryShortcut(AssignmentEntryShortcut assignmentEntryShortcut) {
        String sql = "INSERT INTO assignment_entry_shortcut (teacher_id, value) VALUES " + 
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

    public void updateAssignmentEntryShortcut(AssignmentEntryShortcut assignmentEntryShortcut) {
        String sql = "UPDATE assignment_entry_shortcut SET "
                + "teacher_id = ?, value = ? "
                + "WHERE assignment_entry_shortcut_id = ?;";
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

    public void deleteAssignmentEntryShortcut(AssignmentEntryShortcut assignmentEntryShortcut) {
        String sql = "DELETE FROM assignment_entry_shortcut WHERE assignment_entry_shortcut_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, assignmentEntryShortcut.assignmentEntryShortcutId);
                return ps;
            }
        });
    }
}
