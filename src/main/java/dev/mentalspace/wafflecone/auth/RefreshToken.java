package dev.mentalspace.wafflecone.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.user.User;

public class RefreshToken {
    // One week in millis
    private final long VALIDITY_DURATION = 7*24*60*60*1000; 

    public Long refreshTokenId;
    public Long userId;
    public Long refreshTokenChainId;
    public String tokenString;
    public Long expirationTime;
    public AuthScope permissions;
    public boolean valid;

    public RefreshToken() {
    }

    public RefreshToken(RefreshToken oldToken, String rawApiKey) {
        this.userId = oldToken.userId;
        this.refreshTokenChainId = oldToken.refreshTokenChainId;
        this.tokenString = Utils.hashApiKey(rawApiKey);
        this.expirationTime = oldToken.expirationTime;
        this.permissions = oldToken.permissions;
        this.valid = true;
    }

    public RefreshToken(User user, AuthScope perms, String rawApiKey, long refreshTokenChainId) {
        this.userId = user.userId;
        this.refreshTokenChainId = refreshTokenChainId;
        this.tokenString = Utils.hashApiKey(rawApiKey);
        this.expirationTime = System.currentTimeMillis() + VALIDITY_DURATION;
        this.permissions = perms;
        this.valid = true;
    }

    public static void addCookieHeader(HttpHeaders headers, String rawRefreshApiKey) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		headers.add("Set-Cookie", 
			  "refreshToken=" + rawRefreshApiKey
			+ "; Expires=" + sdf.format(new Date(System.currentTimeMillis())) + " GMT"
			+ "; SameSite=Strict"
			+ "; Path=/auth/token"
			+ "; Secure"
			+ "; HttpOnly");
    }
}