package dev.mentalspace.wafflecone.databaseobject;

import org.json.JSONObject;

public class EmailRegex {
    public Long emailRegexId;
    public Long schoolId;
    public String matchDomain;
    public String regex;
    public Integer permissions;

    public EmailRegex() {
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("emailRegexId",  this.emailRegexId)
            .put("schoolId",      this.schoolId)
            .put("matchDomain",   this.matchDomain)
            .put("regex",         this.regex)
            .put("permissions",   this.permissions);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("emailRegexId",  this.emailRegexId)
            .put("schoolId",      this.schoolId)
            .put("matchDomain",   this.matchDomain)
            .put("regex",         this.regex)
            .put("permissions",   this.permissions);
        return jsonObject;
    }
}
