package dev.mentalspace.wafflecone;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.Response;
import dev.mentalspace.wafflecone.user.UserController;

@RestController
public class WaffleConeController {
	public static Logger logger = LoggerFactory.getLogger(WaffleConeController.class);

	@GetMapping("/test")
	public ResponseEntity<String> test() {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		WaffleConeController.logger.error(sdf.format(new Date(System.currentTimeMillis())));
		return ResponseEntity.status(HttpStatus.OK).body(new Response("success")
				.put("time", sdf.format(new Date(System.currentTimeMillis())) + " GMT").toString());
	}
}