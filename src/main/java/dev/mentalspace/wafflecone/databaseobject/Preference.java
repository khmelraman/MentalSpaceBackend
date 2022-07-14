package dev.mentalspace.wafflecone.databaseobject;

import org.json.JSONObject;

public class Preference {
    public Long preferenceId;
    public Long studentId;
    public Integer assignmentOrder;
    public Integer startType;
    public Long breakLength;
    public Long breakFrequency;

    public Preference() {
    }

    public void update(Preference updPreference) {
        this.preferenceId = updPreference.preferenceId;
		this.studentId = updPreference.studentId;
		if (updPreference.assignmentOrder != null) {
			this.assignmentOrder = updPreference.assignmentOrder;
		}
		if (updPreference.startType != null) {
			this.startType = updPreference.startType;
		}
		if (updPreference.breakLength != null) {
			this.breakLength = updPreference.breakLength;
		}
		if (updPreference.breakFrequency != null) {
			this.breakFrequency = updPreference.breakFrequency;
		}
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("preferenceId",      this.preferenceId)
            .put("studentId",         this.studentId)
            .put("assignmentOrder",   this.assignmentOrder)
            .put("startType",         this.startType)
            .put("breakLength",       this.breakLength)
            .put("breakFrequency",    this.breakFrequency);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("preferenceId",      this.preferenceId)
            .put("studentId",         this.studentId)
            .put("assignmentOrder",   this.assignmentOrder)
            .put("startType",         this.startType)
            .put("breakLength",       this.breakLength)
            .put("breakFrequency",    this.breakFrequency);
        return jsonObject;
    }
}
