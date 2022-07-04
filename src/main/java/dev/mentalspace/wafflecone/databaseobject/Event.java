package dev.mentalspace.wafflecone.databaseobject;

public class Event {
    public long eventId;
    public long studentId;
    public String name;
    public String description;
    public long startTime;
    public long endTime;
    public int recurring;
    public int weeklyDays;
    public int monthlyDays;

    public Event() {
    }
}
