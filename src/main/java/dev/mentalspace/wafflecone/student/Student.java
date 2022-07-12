package dev.mentalspace.wafflecone.student;

import org.json.JSONObject;

import dev.mentalspace.wafflecone.Utils;

public class Student {
    public Long studentId;
    public String canonicalId;
    public String firstName;
    public String lastName;
    public Long phone;
    public Integer grade;

    public Student() {
    }

    public void updateDetails(Student updStudent) {
        if (!Utils.isEmpty(updStudent.canonicalId)) {
            this.canonicalId = updStudent.canonicalId;
        }
        if (!Utils.isEmpty(updStudent.firstName)) {
            this.firstName = updStudent.firstName;
        }
        if (!Utils.isEmpty(updStudent.lastName)) {
            this.lastName = updStudent.lastName;
        }
        if (!(updStudent.phone == null || updStudent.phone == 0)) {
            this.phone = updStudent.phone;
        }
        if (!(updStudent.grade == null || updStudent.grade == 0)) {
            this.grade = updStudent.grade;
        }
    }

    /**
     * Returns a JSON object to be used in a server response
     * 
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
