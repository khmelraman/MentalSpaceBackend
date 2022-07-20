package dev.mentalspace.wafflecone.teacher;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;
import dev.mentalspace.wafflecone.period.Period;
import dev.mentalspace.wafflecone.period.PeriodService;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import dev.mentalspace.wafflecone.assignmentEntryShortcut.*;

@RestController
@RequestMapping(path = { "/api/v0/teacher" })
public class TeacherController {
	@Autowired
	AuthTokenService authTokenService;
	@Autowired
	UserService userService;
	@Autowired
	TeacherService teacherService;
	@Autowired
	PeriodService periodService;
	@Autowired
	AssignmentEntryShortcutService assignmentEntryShortcutService;

	@PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> registerTeacher(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Teacher registerTeacherDetails) {
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

		if (registerTeacherDetails.phone > 9_999_999_999_9999L) {
			JSONObject errors = new JSONObject().put("phone", ErrorString.PHONE_NUMBER_LENGTH);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		if (loggedInUser.teacherId != 0) {
			JSONObject errors = new JSONObject().put("user", ErrorString.ALREADY_INITIALIZED);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		// insert into db
		WaffleConeController.logger.debug("teacherId before insert: " + String.valueOf(loggedInUser.teacherId));
		teacherService.add(registerTeacherDetails);
		userService.updateTeacher(loggedInUser, registerTeacherDetails);
		WaffleConeController.logger
				.debug("teacher.teacherId after insert: " + String.valueOf(registerTeacherDetails.teacherId));
		WaffleConeController.logger.debug("user.teacherId: " + String.valueOf(loggedInUser.teacherId));

		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response("success").put("teacherId", registerTeacherDetails.teacherId).toString());
	}

	@GetMapping("")
	public ResponseEntity<String> teacherDetails(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "teacherId", defaultValue = "-1") long searchTeacherId,
			@RequestParam(value = "canonicalId", defaultValue = "") String searchCanonicalId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if ((searchTeacherId == -1) && (searchCanonicalId.equals(""))) {
			if (loggedInUser.type == UserType.TEACHER) {
				Teacher teacher = teacherService.getById(loggedInUser.teacherId);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response("success").put("teacher", teacher.toJsonObject()).toString());
			}
		}

		if (loggedInUser.type == UserType.TEACHER) {
			if (!teacherService.existsById(searchTeacherId)) {
				JSONObject errors = new JSONObject().put("teacherId", ErrorString.INVALID_ID);
           		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
			}
			Teacher teacher = teacherService.getById(searchTeacherId);
				return ResponseEntity.status(HttpStatus.OK)
						.body(new Response("success").put("teacher", teacher.toJsonObject()).toString());
		}
		// TODO: implement teacher details by teacherId and canonicalId
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
	}

	@PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> patchTeacher(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Teacher patchDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if (patchDetails.phone > 9_999_999_999_9999L) {
			JSONObject errors = new JSONObject().put("phone", ErrorString.PHONE_NUMBER_LENGTH);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (teacherService.existsById(patchDetails.teacherId)) {
			Teacher loggedInTeacher = teacherService.getById(loggedInUser.teacherId);
			loggedInTeacher.updateTeacher(patchDetails);
			teacherService.updateTeacher(loggedInTeacher);
			return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
		}

		// TODO: Debate on if admins can modify teacher accs 
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
	}

	@GetMapping("/classes")
	public ResponseEntity<String> teacherDetails(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "archived", defaultValue = "false") boolean searchArchived,
			@RequestParam(value = "canonicalId", defaultValue = "") String searchCanonicalId,
			@RequestParam(value = "teacherId", defaultValue = "-1") long searchTeacherId) {
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

		if (!teacherService.existsById(searchTeacherId)) {
			if (!teacherService.existsByCanonicalId(searchCanonicalId)) {
				if (loggedInUser.type == UserType.TEACHER) {
					searchTeacherId = loggedInUser.teacherId;
				} else {
					JSONObject errors = new JSONObject().put("teacherId", ErrorString.INVALID_ID);
        			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
				}
			}
			searchTeacherId = teacherService.getByCanonicalId(searchCanonicalId).teacherId;
		}
		List<Period> teacherPeriods = periodService.getByTeacherId(searchTeacherId, searchArchived);
		
		Response response = new Response("success").put("classIds", teacherPeriods);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@GetMapping("/assignmentEntryShortcut")
	public ResponseEntity<String> teacherShortcuts(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "teacherId", defaultValue = "-1") long teacherId) {
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

		if (!teacherService.existsById(teacherId)) {
			if (loggedInUser.type == UserType.TEACHER) {
				teacherId = loggedInUser.teacherId;
			}
			JSONObject errors = new JSONObject().put("teacherId", ErrorString.INVALID_ID);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
		}

		List<AssignmentEntryShortcut> assignmentEntryShortcuts = assignmentEntryShortcutService.getByTeacherId(teacherId);
		Response response = new Response("success").put("assignmentEntryShortcuts", assignmentEntryShortcuts);
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}
}
