
package dev.mentalspace.wafflecone.assignmentEntryShortcut;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.mentalspace.wafflecone.Utils;

public class AssignmentEntryShortcut {
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