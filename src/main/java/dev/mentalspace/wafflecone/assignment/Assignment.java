package dev.mentalspace.wafflecone.assignment;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.mentalspace.wafflecone.Utils;

public class Assignment {
    public Long assignmentId;
    @JsonProperty("classId")
    public Long periodId;
    public Long dateAssigned;
    public Long dateDue;
    public String type;
    public Long estimatedBurden;
    public String name;
    public String description;

    public void updateDetails(Assignment updAssignment) {
        if (updAssignment.dateAssigned != null) {
            this.dateAssigned = updAssignment.dateAssigned;
        }
        if (updAssignment.dateDue != null) {
            this.dateDue = updAssignment.dateDue;
        }
        if (!Utils.isEmpty(updAssignment.type)) {
            this.type = updAssignment.type;
        }
        if (updAssignment.estimatedBurden != null) {
            this.estimatedBurden = updAssignment.estimatedBurden;
        }
        if (!Utils.isEmpty(updAssignment.name)) {
            this.name = updAssignment.name;
        }
        if (!Utils.isEmpty(updAssignment.description)) {
            this.description = updAssignment.description;
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("assignmentId",       this.assignmentId)
            .put("periodId",           this.periodId)
            .put("dateAssigned",       this.dateAssigned)
            .put("dateDue",            this.dateDue)
            .put("type",               this.type)
            .put("estimatedBurden",    this.estimatedBurden)
            .put("name",               this.name)
            .put("description",        this.description);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("assignmentId",       this.assignmentId)
            .put("periodId",           this.periodId)
            .put("dateAssigned",       this.dateAssigned)
            .put("dateDue",            this.dateDue)
            .put("type",               this.type)
            .put("estimatedBurden",    this.estimatedBurden)
            .put("name",               this.name)
            .put("description",        this.description);
        return jsonObject;
    }
}
