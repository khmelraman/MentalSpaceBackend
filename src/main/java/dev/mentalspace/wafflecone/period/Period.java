package dev.mentalspace.wafflecone.period;

import org.json.JSONObject;

import dev.mentalspace.wafflecone.Utils;

public class Period {
    public Long periodId;
    public Long teacherId;
    public Long subjectId;
    public Integer period;
    public String classCode;
    public Boolean archived;

    boolean regenerateClassCode;
    boolean archivedOnly;

    public void setRegenerateClassCode(boolean regenerateClassCode) {
        this.regenerateClassCode = regenerateClassCode;
    }

    public void setArchivedOnly(boolean archivedOnly) {
        this.archivedOnly = archivedOnly;
    }

    public Period() {
    }

    // Used only for response output.
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject()
            .put("classId", this.periodId)
            .put("teacherId", this.teacherId)
            .put("subjectId", this.subjectId)
            .put("period", this.period)
            .put("classCode", this.classCode)
            .put("archived", this.archived);

        return jsonObj;
    }
    
    public void updateDetails(Period updPeriod) {
        if (!(updPeriod.teacherId == null || updPeriod.teacherId == 0)) {
            this.subjectId = updPeriod.subjectId;
        }
        if (!(updPeriod.subjectId == null || updPeriod.subjectId == 0)) {
            this.subjectId = updPeriod.subjectId;
        }
        if (!(updPeriod.period == null || updPeriod.period == 0)) {
            this.period = updPeriod.period;
        }
        if (!(updPeriod.subjectId == null || updPeriod.subjectId == 0)) {
            this.subjectId = updPeriod.subjectId;
        }
        if (updPeriod.archived != null) {
            this.archived = updPeriod.archived;
        }
    }
}
