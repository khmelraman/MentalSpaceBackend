package dev.mentalspace.wafflecone.period;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;
import dev.mentalspace.wafflecone.user.User;
import dev.mentalspace.wafflecone.user.UserService;
import dev.mentalspace.wafflecone.user.UserType;

@RestController
@RequestMapping(path={"/api/v0/class"})
public class PeriodController {
    @Autowired
	UserService userService;
	@Autowired
	RefreshTokenService refreshTokenService;
	@Autowired
	AuthTokenService authTokenService;
    
    @GetMapping(path={""})
    public ResponseEntity<String> periodDetails(
        @RequestHeader("Authorization") String authApiKey,
        @RequestParam(value = "classId", defaultValue = "-1") long searchClassId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (searchClassId <= 0) {
            JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        if (loggedInUser.type == UserType.TEACHER) {
            
        } else if (loggedInUser.type == UserType.STUDENT) {

        }

		// TODO: Implement 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
    }
}
