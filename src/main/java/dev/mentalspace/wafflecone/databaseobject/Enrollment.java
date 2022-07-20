package dev.mentalspace.wafflecone.databaseobject;

import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Enrollment {
    public Long enrollmentId;
    public Long studentId;
    @JsonProperty("classId")
    public Long periodId;
    public Integer studentPreference;

    List<Long> studentIds;

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

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
