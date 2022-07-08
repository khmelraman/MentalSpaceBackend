package dev.mentalspace.wafflecone.subject;

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

import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;
import dev.mentalspace.wafflecone.user.User;
import dev.mentalspace.wafflecone.user.UserService;
import dev.mentalspace.wafflecone.user.UserType;

@RestController
@RequestMapping(path = { "/api/v0/subject" })
public class SubjectController {
	@Autowired
	UserService userService;
	@Autowired
	RefreshTokenService refreshTokenService;
	@Autowired
	AuthTokenService authTokenService;
	@Autowired
	SubjectService subjectService;

	@PostMapping(path = { "" })
	public ResponseEntity<String> createSubject(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Subject createDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);
		// explicit only allow teacher and admin in case of extra types later.
		if (loggedInUser.type != UserType.TEACHER && loggedInUser.type != UserType.ADMIN) {
			JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		// builder method of doing errors; that way all errors are caught at once
		JSONObject errors = new JSONObject();
		HttpStatus returnStatus = HttpStatus.OK; // used to check if error'd

		if (Utils.isEmpty(createDetails.department)) {
			errors = errors.put("department", ErrorString.DEPARTMENT_EMPTY);
			returnStatus = HttpStatus.BAD_REQUEST;
		}
		if (Utils.isEmpty(createDetails.name)) {
			errors = errors.put("name", ErrorString.NAME_EMPTY);
			returnStatus = HttpStatus.BAD_REQUEST;
		}

		// use negated logic for cleanliness
		if (returnStatus != HttpStatus.OK) {
			return ResponseEntity.status(returnStatus).body(new ErrorResponse(errors).toString());
		}

		// add new Subject
		subjectService.addSubject(createDetails);
		WaffleConeController.logger.debug("New Subject ID: " + createDetails.subjectId);

		Response response = new Response("success").put("subjectId", createDetails.subjectId);
		return ResponseEntity.status(returnStatus).body(response.toString());
	}

	@GetMapping("")
	public ResponseEntity<String> getSubject(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "subjectId", defaultValue = "-1") Long subjectId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

		if (subjectId == -1 || !subjectService.existsById(subjectId)) {
			JSONObject errors = new JSONObject().put("subjectId", ErrorString.SUBJECT_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
		}

		Subject subject = subjectService.getById(subjectId);
		return ResponseEntity.status(HttpStatus.OK)
				.body(new Response("success").put("subject", subject.toJsonObject()).toString());
	}

	@PatchMapping("")
	public ResponseEntity<String> patchSubject(@RequestHeader("Authorization") String authApiKey,
			@RequestBody Subject patchDetails) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);
		if (loggedInUser.type != UserType.TEACHER && loggedInUser.type != UserType.ADMIN) {
			JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (!subjectService.existsById(patchDetails.subjectId)) {
			JSONObject errors = new JSONObject().put("subjectId", ErrorString.SUBJECT_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
		}

		Subject subject = subjectService.getById(patchDetails.subjectId);
		subject.updateDetails(patchDetails);
		subjectService.updateSubject(subject);

		return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
	}

	@DeleteMapping("")
	public ResponseEntity<String> deleteSubject(@RequestHeader("Authorization") String authApiKey,
			@RequestParam(value = "subjectId", defaultValue = "-1") long subjectId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);
		if (loggedInUser.type != UserType.TEACHER && loggedInUser.type != UserType.ADMIN) {
			JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (!subjectService.existsById(subjectId)) {
			JSONObject errors = new JSONObject().put("subjectId", ErrorString.SUBJECT_NOT_FOUND);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
		}

		subjectService.deleteSubjectById(subjectId);

		return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
	}
}