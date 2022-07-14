package dev.mentalspace.wafflecone.period;

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
public class PeriodService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM period WHERE period_id = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }

    public boolean existsByClassCode(String code) {
        String sql = "SELECT COUNT(*) FROM period WHERE period_id = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != 0;
    }

    public Period getById(long id) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE period_id = ?;";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return period;
    }

    public Period getById(long id, boolean archived) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE period_id = ?" + (archived ? "" : " AND archived = false") + ";";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return period;
    }

    public Period getByClassCode(String classCode) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE class_code = ?;";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        Period period = jdbcTemplate.queryForObject(sql, rowMapper, classCode);
        return period;
    }

    public List<Period> getByTeacherId(long id) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE teacher_id = ?;";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        List<Period> period = jdbcTemplate.query(sql, rowMapper, id);
        return period;
    }

    public List<Period> getByTeacherId(long id, boolean archived) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE teacher_id = ?"
                + (archived ? " archived = true" : "")
                + ";";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        List<Period> period = jdbcTemplate.query(sql, rowMapper, id);
        return period;
    }

    public List<Period> getByStudentId(long id) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived "
                + "FROM period JOIN enrollment ON period.period_id = enrollment.period_id " + "WHERE student_id = ?;";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        List<Period> period = jdbcTemplate.query(sql, rowMapper, id);
        return period;
    }

    public List<Period> getByStudentId(long id, boolean archived) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived "
                + "FROM period JOIN enrollment ON period.period_id = enrollment.period_id " + "WHERE student_id = ?"
                + (archived ? "" : " AND archived = false") + ";";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        List<Period> period = jdbcTemplate.query(sql, rowMapper, id);
        return period;
    }

    public Period getBySubjectId(long id) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE subject_id = ?;";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return period;
    }

    public Period getBySubjectId(long id, boolean archived) {
        String sql = "SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period "
                + "WHERE subject_id = ?" + (archived ? "" : " AND archived = false") + ";";
        RowMapper<Period> rowMapper = new PeriodRowMapper();
        Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return period;
    }

    public void addPeriod(Period period) {
        String sql = "INSERT INTO period (teacher_id, subject_id, class_code, archived) VALUES (?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, period.teacherId);
                ps.setLong(2, period.subjectId);
                ps.setInt(3, period.period);
                ps.setString(4, period.classCode);
                ps.setBoolean(5, period.archived);
                return ps;
            }
        }, keyHolder);

        period.periodId = keyHolder.getKey().longValue();
    }

    public void updatePeriod(Period period) {
        String sql = "UPDATE period SET " + "teacher_id = ?, subject_id = ?, period = ?, class_code = ?, archived = ? "
                + "WHERE period_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, period.teacherId);
                ps.setLong(2, period.subjectId);
                ps.setInt(3, period.period);
                ps.setString(4, period.classCode);
                ps.setBoolean(5, period.archived);
                ps.setLong(6, period.periodId);
                return ps;
            }
        });
    }

    public void deletePeriod(Period period) {
        String sql = "DELETE FROM period " + "WHERE period_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, period.periodId);
                return ps;
            }
        });
    }
}
