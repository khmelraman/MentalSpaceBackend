package dev.mentalspace.wafflecone.auth;

import org.bouncycastle.crypto.engines.RC2WrapEngine;
import org.springframework.beans.factory.annotation.Autowired;

import dev.mentalspace.wafflecone.Utils;

// debate on renaming into AccessToken
public class AuthToken {
    public Long authTokenId;
    public Long userId;
    public String tokenString;
    public Long expirationTime;
    public boolean valid;
    public AuthScope permissions;

    // valid for 30 minutes - in millis
    public final long VALIDITY_DURATION = 30*60*1000;

    public void loadUsingRefreshToken(RefreshToken refreshToken, String rawApiKey) {
        this.userId = refreshToken.userId;
        this.tokenString = Utils.hashApiKey(rawApiKey);
        this.expirationTime = System.currentTimeMillis() + VALIDITY_DURATION;
        this.valid = true;
        this.permissions = refreshToken.permissions;
    }
}