package dev.mentalspace.wafflecone.user;

public enum UserType {
    STUDENT,
    TEACHER,
    ADMIN;

    private static final UserType[] userTypeValues = UserType.values();

    UserType() {
    }

    public UserType toEnum(String name) {
        switch (name.toUpperCase()) {
            case "STUDENT":
                return STUDENT;
            case "TEACHER":
                return TEACHER;
            case "ADMIN":
                return ADMIN;
            default:
                return STUDENT;
        }
    }

	public String toString() {
		return this.name();
	}

    public static UserType fromInt(int val) {
        return userTypeValues[val];
    }
}
