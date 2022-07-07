package dev.mentalspace.wafflecone.teacher;

import org.json.JSONObject;

public class Teacher {
    public Long teacherId;
    public String canonicalId;
    public String firstName;
    public String lastName;
    public Long phone;
    public String department;

    public Teacher() {
    }

    /**
     * Returns a JSON object to be used in a server response
     * @return JSONObject for embedding into a Response
     */
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject()
            .put("teacherId", this.teacherId)
            .put("canonicalId", canonicalId)
            .put("firstName", this.firstName)
            .put("lastName", this.lastName)
            .put("phone", this.phone)
            .put("department", this.department);
        return jsonObj;
    }
}
