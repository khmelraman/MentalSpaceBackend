package dev.mentalspace.wafflecone.school;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class EmailRegexRowMapper implements RowMapper<EmailRegex> {
    @Override
    public EmailRegex mapRow(ResultSet row, int rowNum) throws SQLException {
        EmailRegex emailRegex = new EmailRegex();
        emailRegex.emailRegexId = row.getLong("email_regex_id");
        emailRegex.schoolId = row.getLong("school_id");
        emailRegex.matchDomain = row.getString("match_domain");
        emailRegex.regex = row.getString("regex");
        emailRegex.permissions = row.getInt("permissions");
        return emailRegex;
    }
}
