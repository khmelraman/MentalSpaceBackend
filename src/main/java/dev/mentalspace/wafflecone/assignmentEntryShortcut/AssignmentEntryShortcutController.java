package dev.mentalspace.wafflecone.assignmentEntryShortcut;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.databaseobject.EnrollmentService;
import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;

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

import dev.mentalspace.wafflecone.user.*;

@RestController
@RequestMapping(path = { "/api/v0/assignmentEntryShortcut" })
public class AssignmentEntryShortcutController {
    @Autowired
    UserService userService;
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    AssignmentEntryShortcutService assignmentEntryShortcutService;

    @GetMapping(path = "")
    public ResponseEntity<String> getAssignmentEntryShortcut(
        @RequestHeader("Authorization") String authApiKey, 
        @RequestParam(value = "assignmentEntryShortcutId", defaultValue = "-1") Long assignmentEntryShortcutId) {
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

        if (!assignmentEntryShortcutService.existsById(assignmentEntryShortcutId)) {
            JSONObject errors = new JSONObject().put("assignmentEntryShortcutId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        AssignmentEntryShortcut assignmentEntryShortcut = assignmentEntryShortcutService.getById(assignmentEntryShortcutId);

        if (loggedInUser.teacherId != assignmentEntryShortcut.teacherId) {
            JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Response response = new Response("success").put("assignmentEntryShortcut", assignmentEntryShortcut.toJsonObject());
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> createAssignmentEntryShortcut(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody AssignmentEntryShortcut createAssignmentEntryShortcut
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

        if (loggedInUser.teacherId != createAssignmentEntryShortcut.teacherId) {
            JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }

        if (Utils.isEmpty(createAssignmentEntryShortcut.value)) {
            JSONObject errors = new JSONObject().put("value", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }

        assignmentEntryShortcutService.addAssignmentEntryShortcut(createAssignmentEntryShortcut);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchAssignmentEntryShortcut(
        @RequestHeader("Authorization") String authApiKey,
        @RequestBody AssignmentEntryShortcut patchDetails) {
        
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
        
        if (!assignmentEntryShortcutService.existsById(patchDetails.assignmentEntryShortcutId)) {
            JSONObject errors = new JSONObject().put("assignmentEntryShortcutId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        AssignmentEntryShortcut assignmentEntryShortcut = assignmentEntryShortcutService.getById(patchDetails.assignmentEntryShortcutId);

        if (loggedInUser.teacherId != assignmentEntryShortcut.teacherId) {
            JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        assignmentEntryShortcut.value = patchDetails.value;

        assignmentEntryShortcutService.updateAssignmentEntryShortcut(assignmentEntryShortcut);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @DeleteMapping(path = "")
    public ResponseEntity<String> deleteAssignment(
        @RequestHeader("Authorization") String authApiKey,
        @RequestParam(value = "assignmentEntryShortcutId", defaultValue = "-1") Long deleteId) {
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
        
        if (!assignmentEntryShortcutService.existsById(deleteId)) {
            JSONObject errors = new JSONObject().put("assignmentEntryShortcutId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        AssignmentEntryShortcut dbShortcut = assignmentEntryShortcutService.getById(deleteId);

        if (loggedInUser.teacherId != dbShortcut.teacherId) {
            JSONObject errors = new JSONObject().put("teacherId", ErrorString.OWNERSHIP);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        assignmentEntryShortcutService.deleteAssignmentEntryShortcut(dbShortcut);
        
        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }
}
