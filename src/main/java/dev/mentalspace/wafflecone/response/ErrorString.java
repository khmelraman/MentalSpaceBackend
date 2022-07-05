package dev.mentalspace.wafflecone.response;

public class ErrorString {
	public static final String PERMISSION_ERROR = "You do not have the permissions to access this resource";

	public static final String USERNAME_EMPTY = emptyString("username");
	public static final String EMAIL_EMPTY = emptyString("email");
	public static final String PASSWORD_EMPTY = emptyString("password");

	public static final String USERNAME_IN_USE = inUseString("Username");
	public static final String EMAIL_IN_USE = inUseString("Email");

	public static final String PASSWORD_LENGTH = "Password length is too short. (req. >=8 characters)";

	public static String inUseString(String field) {
		return field + " is already in use";
	}

	public static String emptyString(String field) {
		return "Required field: " + field + " is empty";
	}
}
