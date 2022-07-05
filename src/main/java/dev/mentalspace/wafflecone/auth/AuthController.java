package dev.mentalspace.wafflecone.auth;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.Response;

@RestController
@RequestMapping(value={"/api/v0/auth"})
public class AuthController {
	@GetMapping("/csrf")
	public ResponseEntity<String> getCsrfToken(CsrfToken token) {
		JSONObject response = new JSONObject()
			.put("csrfToken", token.getToken())
			.put("paramName", token.getParameterName())
			.put("headerName", token.getHeaderName())
			.put("status", "success");
		return ResponseEntity.status(HttpStatus.OK).body(response.toString());
	}

	@PostMapping("/token")
	public ResponseEntity<String> refreshAccessToken(@CookieValue("refreshToken") String refreshToken) {
		return ResponseEntity.status(HttpStatus.OK).body("woah");
	}
}
