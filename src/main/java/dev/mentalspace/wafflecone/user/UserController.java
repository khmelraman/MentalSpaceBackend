package dev.mentalspace.wafflecone.user;


import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;

@RestController
@RequestMapping(path={"/api/v0/user"})
public class UserController {
	@Autowired
	UserService userService;

	@GetMapping("/test")
	public ResponseEntity<String> test(@RequestHeader("Authorization") String apiToken) {
		return ResponseEntity.status(HttpStatus.OK).body(new Response().toString());
	}
	
	/**
	 * Registration path, verifies that all parameters are met then creates user account.
	 * Checks implmented:
	 *   password: exist, length
	 *   username: exist, collision
	 *   email:    exist, collision
	 */
	@PostMapping(path="/register")
	public ResponseEntity<String> userRegister(@RequestBody User registerDetails) {
		// builder method of doing errors; that way all errors are caught at once
		JSONObject errors = new JSONObject();
		HttpStatus returnStatus = HttpStatus.OK; // used to check if error'd
		
		// verification block refactor later into a monad
		if (Utils.isEmpty(registerDetails.password)) {
			errors = errors.put("password", ErrorString.PASSWORD_EMPTY);
			returnStatus = HttpStatus.BAD_REQUEST;
		}
		else if (registerDetails.password.length() < 8) {
			errors = errors.put("password", ErrorString.PASSWORD_LENGTH);
			returnStatus = HttpStatus.BAD_REQUEST;
		}
		if (Utils.isEmpty(registerDetails.email)) {
			errors = errors.put("email", ErrorString.EMAIL_EMPTY);
			returnStatus = HttpStatus.BAD_REQUEST;
		}
		else if (userService.existsByEmail(registerDetails.email)) {
			errors = errors.put("email", ErrorString.EMAIL_IN_USE);
			returnStatus = HttpStatus.CONFLICT;
		}
		if (Utils.isEmpty(registerDetails.username)) {
			errors = errors.put("username", ErrorString.USERNAME_EMPTY);
			returnStatus = HttpStatus.BAD_REQUEST;
		}
		else if (userService.existsByUsername(registerDetails.username)) {
			errors = errors.put("username", ErrorString.USERNAME_IN_USE);
			returnStatus = HttpStatus.CONFLICT;
		}

		// Use of negated logic for cleanliness.
		if (returnStatus != HttpStatus.OK) {
			return ResponseEntity.status(returnStatus).body(new ErrorResponse(errors).toString());
		}

		// // add user to db
		WaffleConeController.logger.error(String.valueOf(registerDetails.emailVerified));
		registerDetails.password = Utils.encodePassword(registerDetails.password);
		WaffleConeController.logger.error(registerDetails.password);
		userService.add(registerDetails);
		
		return ResponseEntity.status(returnStatus).body(new Response("success").toString());
	}

	@PostMapping(path="/login", consumes={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<User> userLogin(@RequestBody User loginDetails) {
		WaffleConeController.logger.warn(loginDetails.password);
		return ResponseEntity.status(HttpStatus.OK).body(loginDetails);
	}
}
