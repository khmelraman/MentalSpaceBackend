package dev.mentalspace.wafflecone.work;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.assignment.Assignment;
import dev.mentalspace.wafflecone.assignment.AssignmentService;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;

import java.util.List;

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
import dev.mentalspace.wafflecone.student.StudentService;
import dev.mentalspace.wafflecone.todo.Todo;
import dev.mentalspace.wafflecone.todo.TodoService;
import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;
import dev.mentalspace.wafflecone.user.User;
import dev.mentalspace.wafflecone.user.UserService;
import dev.mentalspace.wafflecone.user.UserType;
import dev.mentalspace.wafflecone.work.WorkService;
@RestController
@RequestMapping(path = { "/api/v0/work" })
public class WorkController {
    @Autowired
    UserService userService;
    @Autowired
    AuthTokenService authTokenService;
    @Autowired
    TodoService todoService;
    @Autowired
    WorkService workService;
    @Autowired
    AssignmentService assignmentService;
    @Autowired
    StudentService studentService;

    @GetMapping(path = { "" })
	public ResponseEntity<String> getWork(
	    @RequestHeader("Authorization") String authApiKey, 
	    @RequestParam(value = "workId", defaultValue = "-1") Long searchWorkId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if (loggedInUser.type != UserType.STUDENT) {
			JSONObject errors = new JSONObject().put("userType", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
		}

		if (!workService.existsById(searchWorkId)) {
			JSONObject errors = new JSONObject().put("workId", ErrorString.INVALID_ID);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
		}

		Work work = workService.getById(searchWorkId);

		if (work.studentId != loggedInUser.studentId) {
			JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
		}

		Response response = new Response("success").put("work", work.toJsonObject());

		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @GetMapping(path = { "/todos" })
	public ResponseEntity<String> getTodosByWorkId(
	@RequestHeader("Authorization") String authApiKey, 
        @RequestParam(value = "workId", defaultValue = "-1") Long searchWorkId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("userType", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        if (!workService.existsById(searchWorkId)) {
            JSONObject errors = new JSONObject().put("workId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Work work = workService.getById(searchWorkId);

        if (work.studentId != loggedInUser.studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        List<Todo> todos = todoService.getByWorkId(searchWorkId);
        
        Response response = new Response("success").put("todos", todos);
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    // TODO: in debate, decide later
    // @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    // public ResponseEntity<String> addWork(@RequestHeader("Authorization") String authApiKey, 
    // @RequestBody Work work) {
    //  AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
	// 	if (!authToken.valid) {
	// 		JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
	// 		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
	// 	}
	// 	User loggedInUser = userService.getById(authToken.userId);

    //     if (loggedInUser.type != UserType.STUDENT) {
    //         JSONObject errors = new JSONObject().put("type", ErrorString.USER_TYPE);
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
    //     }

    //     if (!studentService.existsById(work.studentId)) {
    //         JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
    //     }

    //     if 
    // }

    @PatchMapping(path = { "" }, consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchWork(
    	@RequestHeader("Authorization") String authApiKey, 
    	@RequestBody Work work) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
        JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
        	return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("type", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        if(!workService.existsById(work.workId)) {
            JSONObject errors = new JSONObject().put("workId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Work dbWork = workService.getById(work.workId);

        if (dbWork.studentId != loggedInUser.studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

		// TODO: refactor into Work as updateWork();
        work.assignmentId = dbWork.assignmentId;
        work.studentId = dbWork.studentId;

        if (work.priority == null) {
            work.priority = dbWork.priority;
        }
        if (work.remainingTime == null) {
            work.remainingTime = dbWork.remainingTime;
        }

        workService.updateWork(work);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @DeleteMapping(path = { "" })
    public ResponseEntity<String> deleteWork(
    	@RequestHeader("Authorization") String authApiKey, 
    	@RequestParam(value = "workId", defaultValue = "[]") List<Long> deleteWorkIds) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("type", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        List<Work> deleteWorks = workService.getByIdList(deleteWorkIds);

        if (deleteWorks.size() < deleteWorkIds.size()) {
            JSONObject errors = new JSONObject().put("todoId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        for (int i = 0; i < deleteWorkIds.size(); i++) {
            if (loggedInUser.studentId != deleteWorks.get(i).studentId) {
                JSONObject errors = new JSONObject().put("studentId", ErrorString.PERMISSION_ERROR);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
        }

        for (int i = 0; i < deleteWorkIds.size(); i++) {
            workService.deleteWork(deleteWorks.get(i));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }
}
