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

@Transactional
@Repository
public class RefreshTokenService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // get authtoken by id, idk why this would ever be used.
    public RefreshToken getById(Long id) {
        String sql = "SELECT user_id, refresh_token_chain_id, token_string, expiration_time, permissions, valid FROM refresh_token WHERE refresh_token_id = ?;";
        RowMapper<RefreshToken> rowMapper = new RefreshTokenRowMapper();
        RefreshToken refreshToken = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return refreshToken;
    }

    public RefreshToken getBySha256Hash(String hashedKey) {
        String sql = "SELECT refresh_token_id, user_id, refresh_token_chain_id, token_string, expiration_time, permissions, valid FROM refresh_token WHERE token_string = ?;";
        RowMapper<RefreshToken> rowMapper = new RefreshTokenRowMapper();
        RefreshToken refreshToken = jdbcTemplate.queryForObject(sql, rowMapper, hashedKey);
        return refreshToken;
    }

    public RefreshToken getByRawKey(String rawKey) {
        String hashedKey = Utils.hashApiKey(rawKey);
        return getBySha256Hash(hashedKey);
    }

    public boolean existsBySha256Hash(String hashedKey) {
        String sql = "SELECT COUNT(*) FROM refresh_token WHERE token_string = ?;";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, hashedKey);
        return count != 0;
    }

    public boolean existsByRawKey(String rawKey) {
        String hashedKey = Utils.hashApiKey(rawKey);
        return existsBySha256Hash(hashedKey);
    }

    public void revokeChainById(long chainId) {
        String sql = "UPDATE refresh_token_chain SET valid = ? WHERE refresh_token_chain_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setBoolean(1, false);
                ps.setLong(2, chainId);
                return ps;
            }
        });
    }

    public void revokeChainByToken(RefreshToken oldToken) {
        revokeChainById(oldToken.refreshTokenChainId);
    }

    public boolean chainValidById(long chainId) {
        String sql = "SELECT valid FROM refresh_token_chain WHERE refresh_token_chain_id = ?;";
        boolean valid = jdbcTemplate.queryForObject(sql, Boolean.class, chainId);
        return valid;
    }

    public boolean chainValidByToken(RefreshToken refreshToken) {
        return chainValidById(refreshToken.refreshTokenChainId);
    }

    public long newRefreshTokenChain() {
        String sql = "INSERT INTO refresh_token_chain (valid)" + " VALUES (?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setBoolean(1, true);
                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void add(RefreshToken refreshToken) {
        String sql = "INSERT INTO refresh_token (user_id, refresh_token_chain_id, token_string, expiration_time, permissions, valid)"
                + " VALUES (?, ?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, refreshToken.userId);
                ps.setLong(2, refreshToken.refreshTokenChainId);
                ps.setString(3, refreshToken.tokenString);
                ps.setLong(4, refreshToken.expirationTime);
                ps.setInt(5, refreshToken.permissions.ordinal());
                ps.setBoolean(6, refreshToken.valid);
                return ps;
            }
        }, keyHolder);

        refreshToken.refreshTokenId = keyHolder.getKey().longValue();
    }

    public void revokeById(long refreshTokenId) {
        String sql = "UPDATE refresh_token SET valid = ? WHERE refresh_token_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setBoolean(1, false);
                ps.setLong(2, refreshTokenId);
                return ps;
            }
        });
    }

    public void revokeByToken(RefreshToken refreshToken) {
        revokeById(refreshToken.refreshTokenId);
    }
}
