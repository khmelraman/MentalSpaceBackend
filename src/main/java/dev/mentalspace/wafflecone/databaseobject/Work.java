package dev.mentalspace.wafflecone.databaseobject;

import org.json.JSONObject;

public class Work {
    public Long workId;
    public Long studentId;
    public Long assignmentId;
    public Long remainingTime;
    public Integer priority;

    public Work() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("workId",        this.workId)
            .put("studentId",     this.studentId)
            .put("assignmentId",  this.assignmentId)
            .put("remainingTime", this.remainingTime)
            .put("priority",      this.priority);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("workId",        this.workId)
            .put("studentId",     this.studentId)
            .put("assignmentId",  this.assignmentId)
            .put("remainingTime", this.remainingTime)
            .put("priority",      this.priority);
        return jsonObject;
    }
}
