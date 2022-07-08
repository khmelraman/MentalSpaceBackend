package dev.mentalspace.wafflecone.period;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;
import dev.mentalspace.wafflecone.databaseobject.EnrollmentService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
@RequestMapping(path = { "/api/v0/class" })
public class PeriodController {
    @Autowired
    UserService userService;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    PeriodService periodService;
    @Autowired
    EnrollmentService enrollmentService;

    @PostMapping(path = { "" })
    public ResponseEntity<String> createPeriod(@RequestHeader("Authorization") String authApiKey,
            @RequestBody Period createDetails) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        // negated logic for cleanliness
        if (loggedInUser.type != UserType.TEACHER) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        // TODO: implement period checks and creation
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
    }

    @GetMapping(path = { "" })
    public ResponseEntity<String> periodDetails(@RequestHeader("Authorization") String authApiKey,
            @RequestParam(value = "classId", defaultValue = "-1") long searchPeriodId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (searchPeriodId <= 0) {
            JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        } else if (!periodService.existsById(searchPeriodId)) {
            JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        if (loggedInUser.type == UserType.TEACHER) {
            Period period = periodService.getById(searchPeriodId);
            if (loggedInUser.teacherId == period.teacherId) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response("success").put("class", period.toJsonObject()).toString());
            } else {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response("success").put("class", period.toJsonObject().put("classCode", "")) // Scrub
                                                                                                               // the
                                                                                                               // class
                                                                                                               // code
                                                                                                               // if
                                                                                                               // non-owning
                                                                                                               // teacher
                                .toString());
            }
        } else if (loggedInUser.type == UserType.STUDENT) {
            // check enrolled
            if (enrollmentService.isEnrolled(loggedInUser.studentId, searchPeriodId)) {
                Period period = periodService.getById(searchPeriodId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new Response("success").put("class", period.toJsonObject().put("classCode", "")) // Scrub
                                                                                                               // the
                                                                                                               // class
                                                                                                               // code
                                                                                                               // if
                                                                                                               // student
                                                                                                               // is
                                                                                                               // fetching
                                                                                                               // this
                                                                                                               // information
                                .toString());
            } else {
                JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString()); // 404
                                                                                                               // for
                                                                                                               // "confidentiality"
            }
        }

        // TODO: Implement
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
    }
}
