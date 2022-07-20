package dev.mentalspace.wafflecone.assignment;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.databaseobject.EnrollmentService;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException.NotFound;

import dev.mentalspace.wafflecone.response.*;
import dev.mentalspace.wafflecone.user.*;
import dev.mentalspace.wafflecone.work.*;
import dev.mentalspace.wafflecone.todo.*;
import dev.mentalspace.wafflecone.assignment.*;
import dev.mentalspace.wafflecone.databaseobject.Enrollment;
import dev.mentalspace.wafflecone.period.Period;
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

    @GetMapping(path = "")
    public ResponseEntity<String> getAssignment(
    	@RequestHeader("Authorization") String authApiKey, 
    	@RequestParam(value = "assignmentId", defaultValue = "-1") Long searchAssignmentId) {
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
            if (periodService.isTeacher(loggedInUser.teacherId, assignment.periodId)) {
                JSONObject errors = new JSONObject().put("teacherId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
        }

        Response response = new Response("success").put("assignment", assignment.toJsonObject());
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> createAssignment(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody Assignment createAssignment
    ) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.TEACHER) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        JSONObject errors = new JSONObject();
        HttpStatus returnStatus = HttpStatus.OK;

        if (!periodService.existsById(createAssignment.periodId)) {
            errors.put("classId", ErrorString.notFound("classId"));
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        if (createAssignment.dateAssigned == null || createAssignment.dateAssigned == 0) {
            createAssignment.dateAssigned = System.currentTimeMillis();
        }
        if (createAssignment.dateDue == null) {
            errors.put("dateDue", ErrorString.notFound("dateDue"));
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        if (createAssignment.estimatedBurden == null) {
            errors.put("estimatedBurden", ErrorString.notFound("estimatedBurden"));
            returnStatus = HttpStatus.BAD_REQUEST;
        }
        if (Utils.isEmpty(createAssignment.name)) {
            errors.put("name", ErrorString.notFound("name"));
            returnStatus = HttpStatus.BAD_REQUEST;
        }

        if (returnStatus != HttpStatus.OK) {
            return ResponseEntity.status(returnStatus).body(new ErrorResponse(errors).toString());
        }

        Period period = periodService.getById(createAssignment.periodId);
        if (period.teacherId != loggedInUser.teacherId) {
            errors = new JSONObject().put("periodId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        assignmentService.addAssignment(createAssignment);
        List<Enrollment> studentEnrollments = enrollmentService.getEnrollmentsByPeriodId(createAssignment.periodId);
        workService.batchAddWorkByEnrollmentsAndAssignment(studentEnrollments, createAssignment);

        return ResponseEntity.status(HttpStatus.OK).body(
            new Response("success")
                .put("assignmentId", createAssignment.assignmentId)
                .toString()
        );
    }

    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchAssignment(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody Assignment patchDetails) {
        
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type == UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        
        Assignment assignment = assignmentService.getById(patchDetails.assignmentId);

        if (loggedInUser.type == UserType.TEACHER) {
            if (!periodService.isTeacher(loggedInUser.teacherId, assignment.periodId)) {
                JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
            }
        }

        assignment.updateDetails(patchDetails);
        assignmentService.updateAssignment(assignment);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @DeleteMapping(path = "")
    public ResponseEntity<String> deleteAssignment(
        @RequestHeader("Authorization") String authApiKey,
        @RequestParam(value = "assignmentId", defaultValue = "-1") Long deleteAssignmentId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.TEACHER) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        if (!assignmentService.existsById(deleteAssignmentId)) {
            JSONObject errors = new JSONObject().put("assignmentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Assignment deleteAssignment = assignmentService.getById(deleteAssignmentId);

        if (periodService.getById(deleteAssignment.periodId).teacherId != loggedInUser.teacherId) {
            JSONObject errors = new JSONObject().put("user", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        assignmentService.deleteAssignment(deleteAssignment);
        
        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }
}
