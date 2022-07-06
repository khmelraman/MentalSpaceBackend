package dev.mentalspace.wafflecone.user;

import org.json.JSONObject;

public class User {
    public Long userId;
    public Integer type;
    public String username;
    public String email;
    public boolean emailVerified;
    String password;
    String csrfToken;

    public Long schoolId;
    public Long teacherId;
    public Long studentId;

    public void setType(String type) {
        switch (type.toUpperCase()) {
            case "STUDENT":
                this.type = 0;
                break;
            case "TEACHER":
                this.type = 0;
                break;
            case "ADMIN":
                this.type = 0;
                break;
            default:
                this.type = 0;
                break;
        }
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    private String typeToString(int val) {
        switch (val) {
            case 0:
                return "Student";
            case 1:
                return "Teacher";
            case 2:
                return "Admin";
            default:
                return "Student";
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject()
            .put("userId", this.userId)
            .put("type", typeToString(this.type))
            .put("username", this.username)
            .put("email", this.email)
            .put("emailVerified", this.emailVerified) // TODO: debate on whether this is an okay default
            .put("schoolId", this.schoolId)
            .put("teacherId", this.teacherId)
            .put("studentId", this.studentId);

        return jsonObj;
    }
}

enum UserType {
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

    public UserType fromInt(int val) {
        return userTypeValues[val];
    }
}
