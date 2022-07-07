package dev.mentalspace.wafflecone.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;

@Transactional
@Repository
public class AuthTokenService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

    // get authtoken by id, idk why this would ever be used.
    public AuthToken getById(Long id) {
        String sql = 
            "SELECT auth_token_id, user_id, token_string, expiration_time, valid, permissions FROM auth_token WHERE auth_token_id = ?;";
        RowMapper<AuthToken> rowMapper = new AuthTokenRowMapper();
        AuthToken authToken = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return authToken;
    }

    public AuthToken getBySha256Hash(String hashedKey) {
        String sql = 
            "SELECT auth_token_id, user_id, token_string, expiration_time, valid, permissions FROM auth_token WHERE token_string = ?;";
        RowMapper<AuthToken> rowMapper = new AuthTokenRowMapper();
        AuthToken authToken = jdbcTemplate.queryForObject(sql, rowMapper, hashedKey);
        return authToken;
    }

    public AuthToken getByRawKey(String rawKey) {
        String hashedKey = Utils.hashApiKey(rawKey);
        return getBySha256Hash(hashedKey);
    }

    public boolean existsBySha256Hash(String hashedKey) {
        String sql = "SELECT COUNT(*) FROM auth_token WHERE token_string = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, hashedKey);
        return count != 0;
    }

    public boolean existsByRawKey(String rawKey) {
        String hashedKey = Utils.hashApiKey(rawKey);
        return existsBySha256Hash(hashedKey);
    }

    public void add(AuthToken authToken) {
        String sql =
			"INSERT INTO auth_token (user_id, refresh_token_chain_id, token_string, expiration_time, valid, permissions)"
				+ " VALUES (?, ?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(
			new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
					PreparedStatement ps =
						connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setLong(1, authToken.userId);
                    ps.setLong(2, authToken.refreshTokenChainId);
					ps.setString(3, authToken.tokenString);
					ps.setLong(4, authToken.expirationTime);
					ps.setBoolean(5, authToken.valid);
					ps.setInt(6, authToken.permissions.ordinal());
					return ps;
				}
		},  keyHolder);

		authToken.authTokenId = keyHolder.getKey().longValue();
    }

    public void revokeByToken(AuthToken authToken) {
        String sql =
        "UPDATE auth_token SET valid = ? WHERE token_string = ?;";
        
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps =
                        connection.prepareStatement(sql);
                    ps.setBoolean(1, false);
                    ps.setString(2, authToken.tokenString);
                    return ps;
                }
        });

    }
    
    // Check that the key is valid
    // Check "valid" and expiration time
    public AuthToken verifyKey(String apiKey) {
        WaffleConeController.logger.debug("Starting Auth Key verification chain");
        String hashedKey = Utils.hashApiKey(apiKey); // save a hash call
        if (existsBySha256Hash(hashedKey)) {
            WaffleConeController.logger.debug("Auth Key exists");
            AuthToken authToken = getBySha256Hash(hashedKey);
            if (authToken.valid) {
                WaffleConeController.logger.debug("Auth Key valid");
                if (authToken.expirationTime > System.currentTimeMillis()) {
                    WaffleConeController.logger.debug("Auth Key unexpired");
                    return authToken;
                }
            }
        }
        // return an auth token with false validity
        AuthToken authToken = new AuthToken();
        authToken.valid = false;
        return authToken;
    }

    public AuthToken verifyBearerKey(String bearerApiKey) {
        String bearerString = bearerApiKey.substring(0, 6);
        if (!bearerString.equals("Bearer")) {
            WaffleConeController.logger.debug("Not Bearer Authentication");
            AuthToken authToken = new AuthToken();
            authToken.valid = false;
            return authToken;
        }

        String apiKey = bearerApiKey.substring(7);
        return verifyKey(apiKey);
    }
}