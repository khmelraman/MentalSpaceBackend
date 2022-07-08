package dev.mentalspace.wafflecone.databaseobject;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.springframework.jdbc.core.RowMapper;

public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet row, int rowNum) throws SQLException {
        Event event = new Event();
        event.eventId = row.getLong("event_id");
        event.studentId = row.getLong("student_id");
        event.name = row.getString("name");
        event.description = row.getString("description");
        event.rruleString = row.getString("rrule_string");
        event.duration = row.getLong("duration");
        try {
            event.setRecurringTime();
        } catch (InvalidRecurrenceRuleException e) {

        }
        return event;
    }
}
