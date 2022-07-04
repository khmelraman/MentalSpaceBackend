package dev.mentalspace.wafflecone.user;

public class User {
    public long userId;
    public int type;
    public String username;
    public String email;
    public boolean emailVerified;
    String password;

    public long schoolId;
    public long teacherId;
    public long studentId;

    public void setPassword(String password) {
        this.password = password;
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
}
