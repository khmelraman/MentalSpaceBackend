package dev.mentalspace.wafflecone.response;

public class ErrorString {
	public static final String PERMISSION_ERROR = "You do not have the permissions to access this resource";

	public static final String USERNAME_EMPTY = emptyString("username");
	public static final String USERNAME_IN_USE = inUseString("Username");
	public static final String INCORRECT_USERNAME = "Incorrect Username";

	public static final String EMAIL_EMPTY = emptyString("email");
	public static final String EMAIL_IN_USE = inUseString("Email");
	public static final String INCORRECT_EMAIL= "Incorrect Email";

	public static final String PASSWORD_EMPTY = emptyString("password");
	public static final String PASSWORD_LENGTH = "Password length is too short. (req. >=8 characters)";
	public static final String INCORRECT_PASSWORD = "Incorrect Password";

	public static final String PHONE_NUMBER_LENGTH = "Phone number is too long";

	public static final String INTERNAL_ERROR_UNREACHABLE = "You've managed to reach a unreachable area of code.";

	public static final String REFRESH_TOKEN_CHAIN_INVALID = "Refresh token chain is invalid.";
	public static final String REFRESH_TOKEN_NOT_FOUND = "Refresh token not found.";
	public static final String REFRESH_TOKEN_USED = "Refresh token has already been used.";

	public static final String INVALID_ACCESS_TOKEN = "Access Token provided was invalid.";

	public static final String USER_TYPE = "Your user type is not valid for this request";
	public static final String ALREADY_INITIALIZED = "This user account is already initialized. Send a PATCH request instead?";

	public static String inUseString(String field) {
		return field + " is already in use";
	}

	public static String emptyString(String field) {
		return "Required field: " + field + " is empty";
	}
}
