package dev.mentalspace.wafflecone.subject;

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
public class SubjectService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Subject getById(long id) {
        String sql = "SELECT subject_id, department, description, name FROM subject WHERE subject_id = ?;";
        RowMapper<Subject> rowMapper = new SubjectRowMapper();
        Subject subject = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return subject;
    }

    public List<Subject> getAll() {
        String sql = "SELECT subject_id, department, description, name FROM subject ORDER BY department;";
        RowMapper<Subject> rowMapper = new SubjectRowMapper();
        List<Subject> subjects = jdbcTemplate.query(sql, rowMapper);
        return subjects;
    }

    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM subject WHERE subject_id = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }

    public boolean existsBySubject(Subject subject) {
        return existsById(subject.subjectId);
    }

    public void addSubject(Subject subject) {
        String sql = "INSERT INTO subject (department, description, name) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, subject.department);
                ps.setString(2, subject.description);
                ps.setString(3, subject.name);
                return ps;
            }
        }, keyHolder);

        subject.subjectId = keyHolder.getKey().longValue();
    }

    public void updateSubject(Subject subject) {
        String sql = "UPDATE subject SET department = ?, description = ?, name = ? WHERE subject_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, subject.department);
                ps.setString(2, subject.department);
                ps.setString(3, subject.description);
                ps.setLong(4, subject.subjectId);
                return ps;
            }
        });
    }

    public void deleteSubjectById(long subjectId) {
        String sql = "DELETE FROM subject WHERE subject_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, subjectId);
                return ps;
            }
        });
    }

    public void deleteSubject(Subject subject) {
        deleteSubjectById(subject.subjectId);
    }
}