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
public class SchoolService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public School getById(long id) {
        String sql = "SELECT school_id, short_name, name, address FROM school WHERE school_id = ?;";
        RowMapper<School> rowMapper = new SchoolRowMapper();
        School school = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return school;
    }

    public void addSchool(School school) {
        String sql = "INSERT INTO school (short_name, name, address) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, school.shortName);
                ps.setString(2, school.name);
                ps.setString(3, school.address);
                return ps;
            }
        }, keyHolder);

        school.schoolId = keyHolder.getKey().longValue();
    }

    public void updateSchool(School school) {
        String sql = "UPDATE school SET short_name = ?, name = ?, address = ? WHERE school_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, school.shortName);
                ps.setString(2, school.name);
                ps.setString(3, school.address);
                ps.setLong(4, school.schoolId);
                return ps;
            }
        });
    }

    public void deleteSchool(School school) {
        String sql = "DELETE FROM school WHERE aschool_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, school.schoolId);
                return ps;
            }
        });
    }
}