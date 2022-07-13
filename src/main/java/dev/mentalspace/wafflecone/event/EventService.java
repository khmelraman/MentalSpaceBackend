package dev.mentalspace.wafflecone.event;

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
public class EventService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean existsById(long id) {
        String sql = "SELECT COUNT(*) FROM event WHERE event_id = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != 0;
    }

    public Event getById(long id) {
        String sql = "SELECT event_id, student_id, name, description, rrule_string, duration FROM event "
                + "WHERE event_id = ?;";
        RowMapper<Event> rowMapper = new EventRowMapper();
        Event event = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return event;
    }

    public List<Event> getByStudentId(long id) {
        String sql = "SELECT event_id, student_id, name, description, rrule_string, duration FROM event "
                + "WHERE student_id = ?;";
        RowMapper<Event> rowMapper = new EventRowMapper();
        List<Event> events = jdbcTemplate.query(sql, rowMapper, id);
        return events;
    }

    public void addEvent(Event event) {
        String sql = "INSERT INTO event (student_id, name, description, rrule_string, duration) VALUES "
                + "(?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, event.studentId);
                ps.setString(2, event.name);
                ps.setString(3, event.description);
                ps.setString(4, event.rruleString);
                ps.setLong(5, event.duration);
                return ps;
            }
        }, keyHolder);

        event.eventId = keyHolder.getKey().longValue();
    }

    public void updateEvent(Event event) {
        String sql = "UPDATE event SET student_id = ?, name = ?, description = ?, rrule_string = ?, duration = ? "
                + "WHERE event_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, event.studentId);
                ps.setString(2, event.name);
                ps.setString(3, event.description);
                ps.setString(4, event.rruleString);
                ps.setLong(5, event.duration);
                ps.setLong(6, event.eventId);
                return ps;
            }
        });
    }

    public void deleteEvent(Event event) {
        String sql = "DELETE FROM event WHERE event_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, event.eventId);
                return ps;
            }
        });
    }
}