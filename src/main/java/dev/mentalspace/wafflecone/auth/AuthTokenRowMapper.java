package dev.mentalspace.wafflecone.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AuthTokenRowMapper implements RowMapper<AuthToken> {
	@Override
	public AuthToken mapRow(ResultSet row, int rowNum) throws SQLException {
		AuthToken authToken = new AuthToken();
		authToken.authTokenId = row.getLong("auth_token_id");
		authToken.userId = row.getLong("user_id");
		authToken.tokenString = row.getString("token_string");
		authToken.expirationTime = row.getLong("expiration_time");
		authToken.valid = row.getBoolean("valid");
		authToken.permissions = AuthScope.fromInt(row.getInt("permissions"));
		return authToken;
	}
}