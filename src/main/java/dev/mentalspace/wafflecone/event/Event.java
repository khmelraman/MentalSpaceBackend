package dev.mentalspace.wafflecone.event;

import java.util.TimeZone;
import org.json.JSONObject;

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
            return -1;
        }
        return next_instance;
    }

    public long getEndTimeForTheDay(long day) {
        if(this.getStartTimeForTheDay(day) == -1) {
            return -1;
        }
        return (this.getStartTimeForTheDay(day) + this.duration);
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("eventId",       this.eventId)
            .put("studentId",     this.studentId)
            .put("name",          this.name)
            .put("description",   this.description)
            .put("rruleString",   "RRULE" + this.rruleString)
            .put("duration",      this.duration);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("eventId",       this.eventId)
            .put("studentId",     this.studentId)
            .put("name",          this.name)
            .put("description",   this.description)
            .put("rruleString",   "RRULE" + this.rruleString)
            .put("duration",      this.duration);
        return jsonObject;
    }
}
