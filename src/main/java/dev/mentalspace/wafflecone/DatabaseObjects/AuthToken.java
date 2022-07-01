public class AuthToken {
    public long authTokenId;
    public long userId;
    public String tokenString;
    public long expirationTime;
    public boolean valid;
    public int permissions;

    public AuthToken() {
    }
}
