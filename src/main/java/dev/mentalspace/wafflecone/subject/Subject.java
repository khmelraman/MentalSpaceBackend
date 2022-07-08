package dev.mentalspace.wafflecone.subject;

import org.json.JSONObject;

import dev.mentalspace.wafflecone.Utils;

public class Subject {
    public Long subjectId;
    public String department;
    public String name;
    public String description;

    public Subject() {
    }

    public void updateDetails(Subject updSubject) {
        if (!Utils.isEmpty(updSubject.department)) {
            this.department = updSubject.department;
        }
        if (!Utils.isEmpty(updSubject.name)) {
            this.name = updSubject.name;
        }
        if (!Utils.isEmpty(updSubject.description)) {
            this.description = updSubject.description;
        }
    }

    // Used only for response output.
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject().put("subjectId", this.subjectId).put("department", this.department)
                .put("name", this.name).put("description", this.description);
        return jsonObj;
    }
}
