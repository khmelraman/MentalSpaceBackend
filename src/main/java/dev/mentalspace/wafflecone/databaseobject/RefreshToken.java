package dev.mentalspace.wafflecone.databaseobject;

public class RefreshToken {
    public long refreshTokenId;
    public long userId;
    public String tokenString;
    public long expirationTime;
    public boolean valid;
    public long nextTokenId;

    public RefreshToken() {
    }
}
