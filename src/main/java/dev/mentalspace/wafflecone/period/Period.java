package dev.mentalspace.wafflecone.period;

public class Period {
    public Long periodId;
    public Long teacherId;
    public Long subjectId;
    public Integer period;
    public String classCode;
    public boolean archived;

    boolean regenerateClassCode;
    
    public void setRegenerateClassCode(boolean regenerateClassCode) {
        this.regenerateClassCode = regenerateClassCode;
    }

    public Period() {
    }
}
