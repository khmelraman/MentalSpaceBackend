package dev.mentalspace.wafflecone.databaseobject;

import org.json.JSONObject;

public class Preference {
    public Long preferenceId;
    public Long studentId;
    public Integer assignmentOrder;
    public Integer startType;
    public Long breakLength;
    public Long breakFrequency;

    public Preference() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("preferenceId",      this.preferenceId)
            .put("studentId",         this.studentId)
            .put("assignmentOrder",   this.assignmentOrder)
            .put("startType",         this.startType)
            .put("breakLength",       this.breakLength)
            .put("breakFrequency",    this.breakFrequency);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("preferenceId",      this.preferenceId)
            .put("studentId",         this.studentId)
            .put("assignmentOrder",   this.assignmentOrder)
            .put("startType",         this.startType)
            .put("breakLength",       this.breakLength)
            .put("breakFrequency",    this.breakFrequency);
        return jsonObject;
    }
}
