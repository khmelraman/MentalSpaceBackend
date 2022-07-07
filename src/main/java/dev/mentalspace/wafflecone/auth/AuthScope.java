package dev.mentalspace.wafflecone.auth;

public enum AuthScope {
    FULL, READ_ONLY, EMAIL_VERIFICATION;

    private static final AuthScope[] enumValues = AuthScope.values();

    AuthScope() {
    }

    public static AuthScope fromInt(int val) {
        return enumValues[val];
    }
}
