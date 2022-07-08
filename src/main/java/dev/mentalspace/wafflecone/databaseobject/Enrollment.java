package dev.mentalspace.wafflecone.databaseobject;

import org.json.JSONObject;

public class Enrollment {
    public Long enrollmentId;
    public Long studentId;
    public Long periodId;
    public Integer studentPreference;

    public Enrollment() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("enrollmentId",      this.enrollmentId)
            .put("studentId",         this.studentId)
            .put("periodId",          this.periodId)
            .put("studentPreference", this.studentPreference);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("enrollmentId",      this.enrollmentId)
            .put("studentId",         this.studentId)
            .put("periodId",          this.periodId)
            .put("studentPreference", this.studentPreference);
        return jsonObject;
    }
}
