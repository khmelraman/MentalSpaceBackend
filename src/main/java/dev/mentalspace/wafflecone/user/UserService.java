package dev.mentalspace.wafflecone.user;

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

import dev.mentalspace.wafflecone.student.Student;
import dev.mentalspace.wafflecone.teacher.Teacher;

@Transactional
@Repository
public class UserService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public User getById(long id) {
		String sql = "SELECT user_id, type, username, email, email_verified, password, school_id, teacher_id, student_id FROM user WHERE user_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public User getByUsername(String username) {
		String sql = "SELECT user_id, type, username, email, email_verified, password, school_id, teacher_id, student_id FROM user WHERE username = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, username);
		return user;
	}

	public User getByEmail(String email) {
		String sql = "SELECT user_id, type, username, email, email_verified, password, school_id, teacher_id, student_id FROM user WHERE email = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, email);
		return user;
	}

	public User getByStudentId(long id) {
		String sql = "SELECT user_id, type, username, email, email_verified, password, school_id, teacher_id FROM user WHERE student_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public User getByTeacherId(long id) {
		String sql = "SELECT user_id, type, username, email, email_verified, password, school_id, student_id FROM user WHERE teacher_id = ?;";
		RowMapper<User> rowMapper = new UserRowMapper();
		User user = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return user;
	}

	public boolean existsByUsername(String username) {
		String sql = "SELECT COUNT(*) FROM user WHERE username = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, username);
		return count != 0;
	}

	public boolean existsByEmail(String email) {
		String sql = "SELECT COUNT(*) FROM user WHERE email = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
		return count != 0;
	}

	public boolean existsById(Long userId) {
		String sql = "SELECT COUNT(*) FROM user WHERE user_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
		return count != 0;
	}

	public void add(User user) {
		String sql = "INSERT INTO user (type, username, email, email_verified, password)" + " VALUES (?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, user.type.ordinal());
				ps.setString(2, user.username);
				ps.setString(3, user.email);
				ps.setBoolean(4, user.emailVerified);
				ps.setString(5, user.password);
				// ps.setLong(6, user.schoolId);
				// ps.setLong(7, user.teacherId);
				// ps.setLong(8, user.studentId);
				return ps;
			}
		}, keyHolder);

		user.userId = keyHolder.getKey().longValue();
	}

	public void updateUser(User user) {
		String sql = "UPDATE user SET username = ?, email = ?, password = ? WHERE user_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, user.username);
				ps.setString(2, user.email);
				ps.setString(3, user.password);
				ps.setLong(4, user.userId);
				return ps;
			}
		});
	}

	public void deleteUser(User user) {
		String sql = "DELETE FROM user WHERE user_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setLong(1, user.userId);
				return ps;
			}
		});
	}

	// Link user to a student_id
	public void updateStudent(User user, Student student) {
		String sql = "UPDATE user SET student_id = ? WHERE user_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setLong(1, student.studentId);
				ps.setLong(2, user.userId);
				return ps;
			}
		});
	}

	// Link user to a teacher_id
	public void updateTeacher(User user, Teacher teacher) {
		String sql = "UPDATE user SET teacher_id = ? WHERE user_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setLong(1, teacher.teacherId);
				ps.setLong(2, user.userId);
				return ps;
			}
		});
	}
}
