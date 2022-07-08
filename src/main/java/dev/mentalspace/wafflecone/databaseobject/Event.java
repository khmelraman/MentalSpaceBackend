package dev.mentalspace.wafflecone.databaseobject;

import java.util.TimeZone;

import org.dmfs.rfc5545.recur.*;

public class Event {
    public Long eventId;
    public Long studentId;
    public String name;
    public String description;
    public String rruleString;
    public Long duration;
    public RecurrenceRule rrule;

    public Event() {
    }

    public void setRecurringTime() throws InvalidRecurrenceRuleException {
        this.rrule = new RecurrenceRule(this.rruleString);
    }

    public long getStartTimeForTheDay(long day) {
        long next_instance = this.rrule.iterator(day, TimeZone.getTimeZone("UTC")).nextMillis();
        if ((next_instance - day) > 86400000) {
            return 0;
        }
        return next_instance;
    }

    public long getEndTimeForTheDay(long day) {
        return (this.getStartTimeForTheDay(day) + this.duration);
    }
}
