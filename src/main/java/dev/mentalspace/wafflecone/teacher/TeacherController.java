package dev.mentalspace.wafflecone.teacher;

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
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping(path={"/api/v0/teacher"})
public class TeacherController {
	@Autowired
	AuthTokenService authTokenService;
	@Autowired
	UserService userService;
	@Autowired
	TeacherService teacherService;

	@PostMapping(path="", consumes={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> registerTeacher(
		@RequestHeader("Authorization") String authApiKey,
		@RequestBody Teacher registerTeacherDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);
		
		// negated logic for clarity
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
		WaffleConeController.logger.debug("teacher.teacherId after insert: " + String.valueOf(registerTeacherDetails.teacherId));
		WaffleConeController.logger.debug("user.teacherId: " + String.valueOf(loggedInUser.teacherId));

		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
			new Response("success")
				.put("teacherId", registerTeacherDetails.teacherId)
				.toString()
		);
	}

	@GetMapping("")
	public ResponseEntity<String> teacherDetails(
		@RequestHeader("Authorization") String authApiKey,
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
				return ResponseEntity.status(HttpStatus.OK).body(
					new Response("success")
						.put("teacher", teacher.toJsonObject())
						.toString()
				);
			}
		}
		// TODO: implement teacher details by teacherId and canonicalId
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
	} 
}
