package dev.mentalspace.wafflecone.databaseobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet row, int rowNum) throws SQLException {
        Event event = new Event();
        event.eventId      = row.getLong   ("event_id");
        event.studentId    = row.getLong   ("student_id");
        event.name         = row.getString ("name");
        event.description  = row.getString ("description");
        event.rrule        = row.getString ("rrule");
        event.duration     = row.getLong   ("duration");
        return event;
    }
}
