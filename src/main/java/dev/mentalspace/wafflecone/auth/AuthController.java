package dev.mentalspace.wafflecone.auth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Lettuce.Cluster.Refresh;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeApplication;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;

@RestController
@RequestMapping(value = { "/api/v0/auth" })
public class AuthController {
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    RefreshTokenService refreshTokenService;

    @GetMapping("/csrf")
    public ResponseEntity<String> getCsrfToken(CsrfToken token) {
        JSONObject response = new JSONObject().put("csrfToken", token.getToken())
                .put("paramName", token.getParameterName()).put("headerName", token.getHeaderName())
                .put("status", "success");
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping("/token")
    public ResponseEntity<String> refreshAccessToken(@CookieValue("refreshToken") String refreshTokenRawKey) {
        WaffleConeController.logger.debug("Recieved Token: " + refreshTokenRawKey);
        // Negated logic for cleanliness
        if (!refreshTokenService.existsByRawKey(refreshTokenRawKey)) {
            JSONObject errors = new JSONObject().put("refreshToken", ErrorString.REFRESH_TOKEN_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        RefreshToken oldRefreshToken = refreshTokenService.getByRawKey(refreshTokenRawKey);
        if (!refreshTokenService.chainValidByToken(oldRefreshToken)) {
            JSONObject errors = new JSONObject().put("refreshToken", ErrorString.REFRESH_TOKEN_CHAIN_INVALID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        } else if (!oldRefreshToken.valid) {
            JSONObject errors = new JSONObject().put("refreshToken", ErrorString.REFRESH_TOKEN_USED);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        } else if (oldRefreshToken.expirationTime < System.currentTimeMillis()) {
            refreshTokenService.revokeChainByToken(oldRefreshToken);
            JSONObject errors = new JSONObject().put("refreshToken", ErrorString.REFRESH_TOKEN_CHAIN_INVALID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        // generate new RefreshToken; invalidate old one
        String rawRefreshApiKey = Utils.generateApiKey();
        RefreshToken newRefreshToken = new RefreshToken(oldRefreshToken, rawRefreshApiKey);
        refreshTokenService.revokeByToken(oldRefreshToken);
        refreshTokenService.add(newRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        RefreshToken.addCookieHeader(headers, rawRefreshApiKey);

        // generate new AuthToken
        AuthToken newAuthToken = new AuthToken();
        String rawAuthApiKey = Utils.generateApiKey();
        newAuthToken.loadUsingRefreshToken(newRefreshToken, rawAuthApiKey);
        authTokenService.add(newAuthToken);

        Response response = new Response("success").put("userId", newRefreshToken.userId)
                .put("accessToken", rawAuthApiKey).put("accessTokenExpiry", newAuthToken.expirationTime)
                .put("refreshTokenExpiry", newRefreshToken.expirationTime);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response.toString());
    }
}
