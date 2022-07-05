package dev.mentalspace.wafflecone.user;

public class AuthToken {
    public Long authTokenId;
    public Long userId;
    public String tokenString;
    public Long expirationTime;
    public boolean valid;
    public Integer permissions;
}
