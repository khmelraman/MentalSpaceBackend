package dev.mentalspace.wafflecone.user;


import dev.mentalspace.wafflecone.WaffleConeController;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.Response;

@RestController
@RequestMapping(value = {"/api/v0/user"})
public class UserController {
	@GetMapping("/test")
	public ResponseEntity<Response> test() {
		return ResponseEntity.status(HttpStatus.OK).body(new Response());
	}

	@PostMapping(path = "/login", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<User> userLogin(@RequestBody User loginDetails) {
		WaffleConeController.logger.warn(loginDetails.password);
		return ResponseEntity.status(HttpStatus.OK).body(loginDetails);
	}
}
