package dev.mentalspace.wafflecone.school;

import org.json.JSONObject;

public class School {
    public Long schoolId;
    public String shortName;
    public String name;
    public String address;

    public School() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("schoolId",  this.schoolId)
            .put("shortName", this.shortName)
            .put("name",      this.name)
            .put("address",   this.address);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("schoolId",  this.schoolId)
            .put("shortName", this.shortName)
            .put("name",      this.name)
            .put("address",   this.address);
        return jsonObject;
    }
}
