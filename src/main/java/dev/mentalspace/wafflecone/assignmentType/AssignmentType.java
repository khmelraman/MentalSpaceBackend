
package dev.mentalspace.wafflecone.assignmentType;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.mentalspace.wafflecone.Utils;

public class AssignmentType {
    public Long assignmentEntryShortcutId;
    public Long teacherId;
    public String value;

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("assignmentEntryShortcutId", this.assignmentEntryShortcutId)
            .put("teacherId",                 this.teacherId)
            .put("value",                     this.value);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("assignmentEntryShortcutId", this.assignmentEntryShortcutId)
            .put("teacherId",                 this.teacherId)
            .put("value",                     this.value);
        return jsonObject;
    }
}