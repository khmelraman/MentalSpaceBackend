package dev.mentalspace.wafflecone.databaseobject;

public enum StartType {
    AS_SOON_AS_POSSIBLE, AS_LATE_AS_POSSIBLE;

    private static final StartType[] userTypeValues = StartType.values();

    public StartType toEnum(String name) {
        switch (name.toUpperCase()) {
        case "AS_SOON_AS_POSSIBLE":
            return AS_SOON_AS_POSSIBLE;
        case "ASAP":
            return AS_SOON_AS_POSSIBLE;
        case "AS_LATE_AS_POSSIBLE":
            return AS_LATE_AS_POSSIBLE;
        case "ALAP":
            return AS_LATE_AS_POSSIBLE;
        default:
            return AS_SOON_AS_POSSIBLE;
    }
    }

    public String toString() {
        return this.name();
    }

    public static StartType fromInt(int val) {
        return userTypeValues[val];
    }
}
