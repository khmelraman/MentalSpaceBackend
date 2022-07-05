package dev.mentalspace.wafflecone.user;

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
}

enum UserType {
    STUDENT(0),
    TEACHER(1),
    ADMIN(2);

    int value;

    UserType(int value) {
        this.value = value;
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

    public UserType toEnum(int value) {
        switch (value) {
            case 0:
                return STUDENT;
            case 1:
                return TEACHER;
            case 2:
                return ADMIN;
            default:
                return STUDENT;
        }
    }


}
