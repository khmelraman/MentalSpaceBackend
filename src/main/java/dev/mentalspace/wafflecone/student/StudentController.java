package dev.mentalspace.wafflecone.student;

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
@RequestMapping(path = { "/api/v0/student" })
public class StudentController {
	@Autowired
	AuthTokenService authTokenService;
	@Autowired
	UserService userService;
	@Autowired
	StudentService studentService;

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

		if (registerStudentDetails.phone > 9_999_999_999_9999L) {
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
		// TODO: implement student details by studentId and canonicalId
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not yet implemented.");
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

		if (patchDetails.phone > 9_999_999_999_9999L) {
			JSONObject errors = new JSONObject().put("phone", ErrorString.PHONE_NUMBER_LENGTH);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (patchDetails.studentId == null || patchDetails.studentId <= 0) {
			Student loggedInStudent = studentService.getById(loggedInUser.studentId);
			loggedInStudent.updateStudent(patchDetails);
			studentService.updateStudent(loggedInStudent);
			return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
		}

		// TODO: Implement modify other people's account(s)
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
	}
}
