package dev.mentalspace.wafflecone.auth;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RefreshTokenRowMapper implements RowMapper<RefreshToken> {
	@Override
	public RefreshToken mapRow(ResultSet row, int rowNum) throws SQLException {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.refreshTokenId = row.getLong("refresh_token_id");
		refreshToken.userId = row.getLong("user_id");
        refreshToken.refreshTokenChainId = row.getLong("refresh_token_chain_id");
		refreshToken.tokenString = row.getString("token_string");
		refreshToken.expirationTime = row.getLong("expiration_time");
		refreshToken.permissions = AuthScope.fromInt(row.getInt("permissions"));
		refreshToken.valid = row.getBoolean("valid");
		return refreshToken;
	}
}