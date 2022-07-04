package dev.mentalspace.wafflecone.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {
	@Override
	public User mapRow(ResultSet row, int rowNum) throws SQLException {
		User user = new User();
		user.userId = row.getLong("user_id");
		user.type = row.getInt("type");
		user.username = row.getString("username");
		user.email = row.getString("email");
		user.emailVerified = row.getBoolean("email_verified");
		user.password = row.getString("password");
		user.schoolId = row.getLong("school_id");
		user.studentId = row.getLong("student_id");
		user.teacherId = row.getLong("teacher_id");
		return user;
	}	
}
