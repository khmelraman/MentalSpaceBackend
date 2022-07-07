package dev.mentalspace.wafflecone.teacher;

import org.json.JSONObject;

import dev.mentalspace.wafflecone.Utils;

public class Teacher {
    public Long teacherId;
    public String canonicalId;
    public String firstName;
    public String lastName;
    public Long phone;
    public String department;

    public Teacher() {
    }

    public void updateTeacher(Teacher updTeacher) {
        if (!Utils.isEmpty(updTeacher.canonicalId)) {
            this.canonicalId = updTeacher.canonicalId;
        }
        if (!Utils.isEmpty(updTeacher.firstName)) {
            this.firstName = updTeacher.firstName;
        }
        if (!Utils.isEmpty(updTeacher.lastName)) {
            this.lastName = updTeacher.lastName;
        }
        if (!(updTeacher.phone == null || updTeacher.phone == 0)) {
            this.phone = updTeacher.phone;
        }
        if (!Utils.isEmpty(updTeacher.department)) {
            this.department = updTeacher.department;
        }
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
