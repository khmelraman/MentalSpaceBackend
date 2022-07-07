package dev.mentalspace.wafflecone.student;

import org.json.JSONObject;

public class Student {
    public Long studentId;
    public String canonicalId;
    public String firstName;
    public String lastName;
    public Long phone;
    public Integer grade;

    public Student() {
    }

    /**
     * Returns a JSON object to be used in a server response
     * @return JSONObject for embedding into a Response
     */
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject()
            .put("studentId", this.studentId)
            .put("canonicalId", canonicalId)
            .put("firstName", this.firstName)
            .put("lastName", this.lastName)
            .put("phone", this.phone)
            .put("grade", this.grade);
        return jsonObj;
    }
}
