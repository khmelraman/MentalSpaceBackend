package dev.mentalspace.wafflecone.student;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;
import dev.mentalspace.wafflecone.event.EventService;

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
import dev.mentalspace.wafflecone.user.User;
import dev.mentalspace.wafflecone.user.UserService;
import dev.mentalspace.wafflecone.user.UserType;
import dev.mentalspace.wafflecone.period.*;
import dev.mentalspace.wafflecone.event.*;
import dev.mentalspace.wafflecone.todo.*;
import dev.mentalspace.wafflecone.work.*;
import dev.mentalspace.wafflecone.databaseobject.*;

@RestController
@RequestMapping(path = { "/api/v0/student" })
public class StudentController {
	@Autowired
	AuthTokenService authTokenService;
	@Autowired
	UserService userService;
	@Autowired
	StudentService studentService;
	@Autowired
	PeriodService periodService;
	@Autowired
	EventService eventService;
	@Autowired
	PreferenceService preferenceService;
	@Autowired
	TodoService todoService;
	@Autowired
	WorkService workService;

	@PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> registerStudent(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Student registerStudentDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		// negated logic for clarity
		if (loggedInUser.type != UserType.STUDENT) {
			JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (registerStudentDetails.phone != null && registerStudentDetails.phone > 9_999_999_999_9999L) {
			JSONObject errors = new JSONObject().put("phone", ErrorString.PHONE_NUMBER_LENGTH);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (loggedInUser.studentId != 0) {
			JSONObject errors = new JSONObject().put("user", ErrorString.ALREADY_INITIALIZED);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		// insert into db
		studentService.add(registerStudentDetails);
		userService.updateStudent(loggedInUser, registerStudentDetails);

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response("success").put("studentId", registerStudentDetails.studentId).toString());
	}

	@GetMapping("")
	public ResponseEntity<String> studentDetails(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "studentId", defaultValue = "-1") long searchStudentId,
			@RequestParam(value = "canonicalId", defaultValue = "") String searchCanonicalId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if ((searchStudentId == -1) && (searchCanonicalId.equals(""))) {
			if (loggedInUser.type == UserType.STUDENT) {
				Student student = studentService.getById(loggedInUser.studentId);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response("success").put("student", student.toJsonObject()).toString());
			}
		}
		
		// TODO: debate whether any account should view any student that they want
		// only allow non-student to view others
		if (loggedInUser.type == UserType.STUDENT) {
			JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (searchStudentId == -1) {
			if (!studentService.existsByCanonicalId(searchCanonicalId)) {
				JSONObject errors = new JSONObject().put("canonicalId", ErrorString.INVALID_ID);
            	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
			}
			Response response = new Response("success").put("student", studentService.getByCanonicalId(searchCanonicalId).toJsonObject());
			return ResponseEntity.status(HttpStatus.OK).body(response.toString());
		}
		if (!studentService.existsById(searchStudentId)) {
			JSONObject errors = new JSONObject().put("canonicalId", ErrorString.INVALID_ID);
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
		}
		Response response = new Response("success").put("student", studentService.getById(searchStudentId).toJsonObject());
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> patchStudent(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Student patchDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if (patchDetails.phone != null && patchDetails.phone > 9_999_999_999_9999L) {
			JSONObject errors = new JSONObject().put("phone", ErrorString.PHONE_NUMBER_LENGTH);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (patchDetails.studentId == null || patchDetails.studentId <= 0) {
			Student loggedInStudent = studentService.getById(loggedInUser.studentId);
			loggedInStudent.updateDetails(patchDetails);
			studentService.updateStudent(loggedInStudent);
			return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
		}

		// TODO: Debate if teachers can modify student accounts
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
	}

	@GetMapping(path = "/classes")
	public ResponseEntity<String> getClasses(@RequestHeader("Authorization") String authApiKey,
		@RequestParam(value = "archived", defaultValue = "false") Boolean archived,
		@RequestParam(value = "canonicalId", defaultValue = "")  String canonicalId,
		@RequestParam(value = "studentId", defaultValue = "-1")  Long studentId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if(!studentService.existsById(studentId)) {
			if (!studentService.existsByCanonicalId(canonicalId)) {
				if (loggedInUser.type == UserType.STUDENT) {
					studentId = loggedInUser.studentId;
				} else {
					JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
				}
			}
			studentId = studentService.getByCanonicalId(canonicalId).studentId;
		}

		if (loggedInUser.type == UserType.TEACHER || loggedInUser.type == UserType.ADMIN) {
			List<Period> classes = periodService.getByStudentId(studentId, archived);
			Response response = new Response().put("classes", classes);
			return ResponseEntity.status(HttpStatus.OK).body(response.toString());
        }
		
		List<Period> classes = periodService.getByStudentId(loggedInUser.studentId, archived);
		Response response = new Response().put("classes", classes);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@GetMapping(path = "/events")
	public ResponseEntity<String> getEvents(@RequestHeader("Authorization") String authApiKey,
		@RequestParam(value = "studentId", defaultValue = "-1") Long studentId) {
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

		studentId = loggedInUser.studentId;

		List<Event> events = eventService.getByStudentId(studentId);
		Response response = new Response().put("events", events);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@GetMapping(path = "/preference")
	public ResponseEntity<String> getPreference(@RequestHeader("Authorization") String authApiKey,
		@RequestParam(value = "studentId", defaultValue = "-1") Long studentId) {
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


		//TODO: debate on letting teachers see student preference
		studentId = loggedInUser.studentId;

		Preference preference = preferenceService.getByStudentId(studentId);
		Response response = new Response().put("preference", preference.toJsonObject());
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@PatchMapping(path = "/preference", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchPreference(
    	@RequestHeader("Authorization") String authApiKey, 
        @RequestBody Preference patchDetails) {
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

        Preference preference = preferenceService.getByStudentId(loggedInUser.studentId);
		preference.update(patchDetails);
		
		preferenceService.updatePreference(preference);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

	@GetMapping(path = "/work")
	public ResponseEntity<String> getWorks(@RequestHeader("Authorization") String authApiKey,
		@RequestParam(value = "studentId", defaultValue = "-1") Long studentId,
		@RequestParam(value = "outstanding", defaultValue = "true") Boolean outstanding) {
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

		studentId = loggedInUser.studentId;

		List<Work> works = workService.getByStudentId(studentId, outstanding);
		Response response = new Response().put("work", works);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@GetMapping(path = "/todos")
	public ResponseEntity<String> getTodos(
		@RequestHeader("Authorization") String authApiKey,
		@RequestParam(value = "startDate", defaultValue = "-1") Long startDate,
		@RequestParam(value = "endDate", defaultValue = "-1") Long endDate,
		@RequestParam(value = "studentId", defaultValue = "-1") Long studentId) {
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
		
		List<Todo> todos = todoService.getByStudentId(studentId, startDate, endDate);
		Response response = new Response().put("todos", todos);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}
}
