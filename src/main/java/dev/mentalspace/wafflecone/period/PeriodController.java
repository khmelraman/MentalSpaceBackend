package dev.mentalspace.wafflecone.period;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.assignment.Assignment;
import dev.mentalspace.wafflecone.assignment.AssignmentService;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;
import dev.mentalspace.wafflecone.databaseobject.Enrollment;
import dev.mentalspace.wafflecone.databaseobject.EnrollmentService;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
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
import dev.mentalspace.wafflecone.subject.SubjectService;
import dev.mentalspace.wafflecone.teacher.Teacher;
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
    @Autowired
    SubjectService subjectService;
    @Autowired
    AssignmentService assignmentService;

    @PostMapping(path = { "" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
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

        if (!subjectService.existsById(createDetails.subjectId)) {
            JSONObject errors = new JSONObject().put("subjectId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        if (createDetails.period == null) {
            JSONObject errors = new JSONObject().put("period", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }

        createDetails.classCode = Utils.generateApiKey().substring(0,6);

        periodService.addPeriod(createDetails);

        return ResponseEntity.status(HttpStatus.OK).body(
            new Response("success")
                .put("periodId", createDetails.periodId)
                .toString()
        );
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
                        // Scrub the class code if non-owning teacher
                        .body(new Response("success").put("class", period.toJsonObject().put("classCode", ""))
                                .toString());
            }
        } else if (loggedInUser.type == UserType.STUDENT) {
            // check enrolled
            if (enrollmentService.isEnrolled(loggedInUser.studentId, searchPeriodId)) {
                Period period = periodService.getById(searchPeriodId);
                return ResponseEntity.status(HttpStatus.OK)
                        // Scrub the class code if student is fetching
                        .body(new Response("success").put("class", period.toJsonObject().put("classCode", ""))
                                .toString());
            } else {
                JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
                // 404 for "confidentiality"
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
            }
        }

        // TODO: Implement Admin stuff
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
    }

    @PatchMapping(path = {""}, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchPeriod(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody Period patchDetails) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (!periodService.existsById(patchDetails.periodId)) {
            JSONObject errors = new JSONObject().put("periodId", ErrorString.PERIOD_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }
        
        Period period = periodService.getById(patchDetails.periodId);

        period.updateDetails(patchDetails);
        if (patchDetails.regenerateClassCode) {
            String newCode = Utils.generateApiKey().substring(0,6);
            // 10 times should hopefully be enough to get a unique class code
            boolean changed = false;
            for (int i = 0; i <= 10; i++) {
                if (!periodService.existsByClassCode(newCode)) {
                    period.classCode = newCode;
                    changed = true;
                    break;
                }
            }
            if (!changed) {
                WaffleConeController.logger.error("Class Code was not changed when it should have been");
                JSONObject errors = new JSONObject().put("regenerateClassCode", ErrorString.COULD_NOT_REGENERATE_CLASS_CODE);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errors).toString());
            }
        }
        periodService.updatePeriod(period);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());    
    }

    @DeleteMapping(path = {""})
    public ResponseEntity<String> deletePeriod(
        @RequestHeader("Authorization") String authApiKey,
        @RequestParam(value = "classId", defaultValue = "-1") Long deletePeriodId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (!periodService.existsById(deletePeriodId)) {
            JSONObject errors = new JSONObject().put("periodId", ErrorString.PERIOD_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }
        if (loggedInUser.type != UserType.TEACHER) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }
        if (loggedInUser.type == UserType.ADMIN) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
        }

        Period period = periodService.getById(deletePeriodId);
        
        if (loggedInUser.teacherId != period.teacherId) {
            JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        periodService.deletePeriod(period);

        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
    }

    @GetMapping(path = { "/assignments" })
    public ResponseEntity<String> periodAssignments(@RequestHeader("Authorization") String authApiKey,
            @RequestParam(value = "classId", defaultValue = "-1") long searchPeriodId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type == UserType.TEACHER) {
            Period period = periodService.getById(searchPeriodId);
            if (loggedInUser.teacherId != period.teacherId) {
                // If non-owning teacher
                JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());    
            }
        } else if (loggedInUser.type == UserType.STUDENT) {
            // check enrolled, negated logic for cleanliness
            if (!enrollmentService.isEnrolled(loggedInUser.studentId, searchPeriodId)) {
                JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
        }

        if (!periodService.existsById(searchPeriodId)) {
            JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        List<Assignment> assignments = assignmentService.getByPeriodId(searchPeriodId);
        Response response = new Response("success").put("classIds", assignments);
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }


    @GetMapping(path = { "/students" })
    public ResponseEntity<String> periodStudents(@RequestHeader("Authorization") String authApiKey,
            @RequestParam(value = "classId", defaultValue = "-1") long searchPeriodId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        // Debate on letting students see this resource
        if(loggedInUser.type == UserType.STUDENT){
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        if (!periodService.existsById(searchPeriodId)) {
            JSONObject errors = new JSONObject().put("classId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }
        
        Period periods = periodService.getBySubjectId(searchPeriodId);

        Response response = new Response("success").put("students", periods.toJsonObject());
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping(path = "/join", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> joinPeriod(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody Period joinPeriod
    ) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        if (!periodService.existsByClassCode(joinPeriod.classCode)) {
            JSONObject errors = new JSONObject().put("classCode", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Period period = periodService.getByClassCode(joinPeriod.classCode);
        enrollmentService.addEnrollment(loggedInUser.studentId, period.periodId, 0);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
        // TODO: Debate on implementing allowing admin/teachers to add students
    }

    @PostMapping(path = "/kick", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> kickStudent(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody Enrollment kickStudent) {
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
        if (periodService.getById(kickStudent.periodId).teacherId != loggedInUser.teacherId) {
            JSONObject errors = new JSONObject().put("user", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }

        enrollmentService.kickStudents(kickStudent);
        
        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }
}
