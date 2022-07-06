package dev.mentalspace.wafflecone.user;


import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.auth.RefreshToken;
import dev.mentalspace.wafflecone.auth.RefreshTokenService;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;

@RestController
@RequestMapping(path={"/api/v0/user"})
public class UserController {
	@Autowired
	UserService userService;
	@Autowired
	RefreshTokenService refreshTokenService;
	@Autowired
	AuthTokenService authTokenService;

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
	@PostMapping(path="/register", consumes={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> userRegister(@RequestBody User registerDetails) {
		// builder method of doing errors; that way all errors are caught at once
		JSONObject errors = new JSONObject();
		HttpStatus returnStatus = HttpStatus.OK; // used to check if error'd

		//TODO: either frontend implements username, or we delete username properly
		registerDetails.username = registerDetails.email;
		
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

		// add user to db
		registerDetails.password = Utils.encodePassword(registerDetails.password);
		userService.add(registerDetails);
		
		Response response = new Response("success").put("user_id", registerDetails.userId);
		return ResponseEntity.status(returnStatus).body(response.toString());
	}

	// check that the user exists and the password matches
	// If so, create a new refresh token chain and access token
	@PostMapping(path="/login", consumes={MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<String> userLogin(@RequestBody User loginDetails) {
		User checkUser;
		boolean userValid = false;
	
		if (userService.existsByUsername(loginDetails.email)) {
			checkUser = userService.getByUsername(loginDetails.email);
			if (Utils.matchesPassword(loginDetails.password, checkUser.password)) {
				userValid = true;
			} else {
				JSONObject errors = new JSONObject().put("password", ErrorString.INCORRECT_PASSWORD);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
			}
		} else if (userService.existsByEmail(loginDetails.email)) {
			checkUser = userService.getByEmail(loginDetails.email);
			if (Utils.matchesPassword(loginDetails.password, checkUser.password)) {
				userValid = true;
			} else {
				JSONObject errors = new JSONObject().put("password", ErrorString.INCORRECT_PASSWORD);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
			}
		} else {
			JSONObject errors = new JSONObject().put("email", ErrorString.INCORRECT_EMAIL);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}

		if (userValid) {
			long refreshTokenChainId = refreshTokenService.newRefreshTokenChain();
			String rawRefreshApiKey = Utils.generateApiKey();
			RefreshToken newRefreshToken = new RefreshToken(checkUser, AuthScope.FULL, rawRefreshApiKey, refreshTokenChainId);
			HttpHeaders headers = new HttpHeaders();
			RefreshToken.addCookieHeader(headers, rawRefreshApiKey);
			refreshTokenService.add(newRefreshToken);
	
			AuthToken newAuthToken = new AuthToken();
			String rawAuthApiKey = Utils.generateApiKey();
			newAuthToken.loadUsingRefreshToken(newRefreshToken, rawAuthApiKey);
			authTokenService.add(newAuthToken);
	
			Response response = new Response("success")
				.put("userId", checkUser.userId)
				.put("accessToken", rawAuthApiKey)
				.put("accessTokenExpiry", newAuthToken.expirationTime)
				.put("refreshTokenExpiry", newRefreshToken.expirationTime);
			return ResponseEntity.status(HttpStatus.OK).headers(headers).body(response.toString());
		}
		WaffleConeController.logger.error("USER LOGIN UNREACHABLE CODE REACHED");
		JSONObject errors = new JSONObject().put("server", ErrorString.INTERNAL_ERROR_UNREACHABLE);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errors).toString());
	}

	@GetMapping("")
	public ResponseEntity<String> getUserDetails(
		@RequestHeader("Authorization") String authApiKey, 
		@RequestParam(value = "userId", defaultValue = "-1") long searchUserId,
		@RequestParam(value = "canonicalId", defaultValue = "") String searchCanonicalId) {
			AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
			if (!authToken.valid) {
				JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
			}
			User loggedInUser = userService.getById(authToken.userId);

			if (searchUserId == -1) {
				Response response = new Response("success").put("user", loggedInUser.toJsonObject());
				return ResponseEntity.status(HttpStatus.OK).body(response.toString());
			}
			// TODO: Implement searching by IDs
			return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("Not Implemented Yet.");
		}
}
