package dev.mentalspace.wafflecone.assignment;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;

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

import dev.mentalspace.wafflecone.response.*;
import dev.mentalspace.wafflecone.user.*;
import dev.mentalspace.wafflecone.work.*;
import dev.mentalspace.wafflecone.todo.*;
import dev.mentalspace.wafflecone.assignment.*;
// TODO: when enrollment service moves to /enrollment/* change */
import dev.mentalspace.wafflecone.databaseobject.*;
import dev.mentalspace.wafflecone.period.PeriodService;

@RestController
@RequestMapping(path = { "/api/v0/assignment" })
public class AssignmentController {
    @Autowired
    UserService userService;
    @Autowired
    WorkService workService;
    @Autowired
    TodoService todoService;
    @Autowired
    AssignmentService assignmentService;
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    PeriodService periodService;

    @GetMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> getTodo(
    	@RequestHeader("Authorization") String authApiKey, 
    	@RequestParam(value = "assignment", defaultValue = "-1") Long searchAssignmentId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

        if (!assignmentService.existsById(searchAssignmentId)) {
            JSONObject errors = new JSONObject().put("assignmentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        Assignment assignment = assignmentService.getById(searchAssignmentId);

        if (loggedInUser.type == UserType.STUDENT) {
            if (!enrollmentService.isEnrolled(loggedInUser.studentId, assignment.periodId)) {
                JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
        }
        if (loggedInUser.type == UserType.TEACHER) {
	    // TODO: refactor into periodService.isTeacher(teacherId, periodId);
            if (periodService.getById(assignment.periodId).teacherId != loggedInUser.teacherId) {
                JSONObject errors = new JSONObject().put("teacherId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
        }

        Response response = new Response("success").put("assignment", assignment.toJsonObject());
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }
}
