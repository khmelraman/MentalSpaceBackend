package dev.mentalspace.wafflecone.databaseobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.List;

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
public class PreferenceService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Preference getById(long id) {
        String sql = "SELECT preference_id, student_id, assignment_order, start_type, break_length, break_frequency "
                + "FROM preference WHERE preference_id = ?;";
        RowMapper<Preference> rowMapper = new PreferenceRowMapper();
        Preference preference = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return preference;
    }

    public Preference getByStudentId(long id) {
        String sql = "SELECT preference_id, student_id, assignment_order, start_type, break_length, break_frequency "
                + "FROM preference WHERE student_id = ?;";
        RowMapper<Preference> rowMapper = new PreferenceRowMapper();
        Preference preference = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return preference;
    }

    public void addPreference(Preference preference) {
        String sql = "INSERT INTO preference (student_id, assignment_order, start_type, break_length, break_frequency) VALUES "
                + "(?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, preference.studentId);
                ps.setInt(2, preference.assignmentOrder.ordinal());
                ps.setInt(3, preference.startType.ordinal());
                ps.setLong(4, preference.breakLength);
                ps.setLong(5, preference.breakFrequency);
                return ps;
            }
        }, keyHolder);

        preference.preferenceId = keyHolder.getKey().longValue();
    }

    public void updatePreference(Preference preference) {
        String sql = "UPDATE preference SET "
                + "student_id = ?, assignment_order = ?, start_type = ?, break_length = ?, break_frequency = ? "
                + "WHERE preference_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, preference.studentId);
                ps.setInt(2, preference.assignmentOrder.ordinal());
                ps.setInt(3, preference.startType.ordinal());
                ps.setLong(4, preference.breakLength);
                ps.setLong(5, preference.breakFrequency);
                ps.setLong(6, preference.preferenceId);
                return ps;
            }
        });
    }

    public void deletePreference(Preference preference) {
        String sql = "DELETE FROM preference WHERE preference_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, preference.preferenceId);
                return ps;
            }
        });
    }
}