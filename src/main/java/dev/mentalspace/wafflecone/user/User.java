package dev.mentalspace.wafflecone.user;

import org.json.JSONObject;

import dev.mentalspace.wafflecone.Utils;

public class User {
    public Long userId;
    public UserType type;
    public String username;
    public String email;
    public boolean emailVerified;
    String password;

    public Long schoolId;
    public Long teacherId;
    public Long studentId;

    // For patchUser
    String newPassword;
    String currentPassword;

    public void setType(String type) {
        this.type = UserType.valueOf(type.toUpperCase());
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void updateDetails(User updUser) {
        if (!Utils.isEmpty(updUser.username)) {
            this.username = updUser.username;
        }
        if (!Utils.isEmpty(updUser.email)) {
            this.email = updUser.email;
        }
        if (!Utils.isEmpty(updUser.newPassword)) {
            this.password = Utils.encodePassword(updUser.newPassword);
        }
    }

    // Used only for response output.
    public JSONObject toJsonObject() {
        JSONObject jsonObj = new JSONObject()
            .put("userId", this.userId)
            .put("type", Utils.capFirstLetter(this.type.toString()))
            .put("username", this.username)
            .put("email", this.email) // TODO: debate on whether this is a sane default
            .put("emailVerified", this.emailVerified)
            .put("schoolId", this.schoolId)
            .put("teacherId", this.teacherId)
            .put("studentId", this.studentId);

        return jsonObj;
    }
}