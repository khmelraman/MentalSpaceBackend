package dev.mentalspace.wafflecone;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

public class Utils {
	static final Argon2PasswordEncoder passwordEncoder = new Argon2PasswordEncoder();
	static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
	static final Base64.Decoder base64Decoder = Base64.getUrlDecoder();
	static final SecureRandom secureRandom = new SecureRandom();

	static MessageDigest getSha256Digester() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			// SHA-256 is "guaranteed" to be supported, but just in case; log the error.
			WaffleConeController.logger.error("MessageDigest SHA-256 Algorithm not found.");
			return null;
		}
	}

	// create 256 bit key
	public static String generateApiKey() {
		byte[] arr = new byte[32]; // 256 bit array
		secureRandom.nextBytes(arr);
		return base64Encoder.encodeToString(arr);
	}

	public static String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	public static boolean matchesPassword(String password, String hash) {
		return passwordEncoder.matches(password, hash);
	}

	public static String hashApiKey(String apiKey) {
		return base64Encoder.encodeToString(getSha256Digester().digest(base64Decoder.decode(apiKey)));
	}

	public static boolean matchesApiKey(String key, String hash) {
		return hash.equals(hashApiKey(key));
	}

	public static boolean isEmpty(String string) {
		return string == null || string.equals("");
	}
}
