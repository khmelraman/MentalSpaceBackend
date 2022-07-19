package dev.mentalspace.wafflecone.databaseobject;

public enum AssignmentOrder {
    SHORT_JOB_FIRST, LONG_JOB_FIRST, IN_SUBJECTS_ORDER;

    private static final AssignmentOrder[] userTypeValues = AssignmentOrder.values();

    public AssignmentOrder toEnum(String name) {
        switch (name.toUpperCase()) {
        case "SHORT_JOB_FIRST":
            return SHORT_JOB_FIRST;
        case "LONG_JOB_FIRST":
            return LONG_JOB_FIRST;
        case "IN_SUBJECTS_ORDER":
            return IN_SUBJECTS_ORDER;
        default:
            return IN_SUBJECTS_ORDER;
    }
    }

    public String toString() {
        return this.name();
    }

    public static AssignmentOrder fromInt(int val) {
        return userTypeValues[val];
    }  
}
