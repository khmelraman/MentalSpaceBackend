package dev.mentalspace.wafflecone.student;

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
public class StudentService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Student getById(long id) {
		String sql = "SELECT student_id, canonical_id, first_name, last_name, phone, grade FROM student WHERE student_id = ?;";
		RowMapper<Student> rowMapper = new StudentRowMapper();
		Student student = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return student;
	}

	public boolean existsById(long id) {
		String sql = "SELECT COUNT(*) FROM student WHERE student_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}

	public void updateStudent(Student student) {
		String sql = "UPDATE student SET canonical_id = ?, first_name = ?, last_name = ?, phone = ?, grade = ? WHERE student_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, student.canonicalId);
				ps.setString(2, student.firstName);
				ps.setString(3, student.lastName);
				ps.setLong(4, student.phone);
				ps.setInt(5, student.grade);
				ps.setLong(6, student.studentId);
				return ps;
			}
		});
	}

	public void add(Student student) {
		String sql = "INSERT INTO student (canonical_id, first_name, last_name, phone, grade)"
				+ " VALUES (?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, student.canonicalId);
				ps.setString(2, student.firstName);
				ps.setString(3, student.lastName);
				ps.setLong(4, student.phone);
				ps.setInt(5, student.grade);
				return ps;
			}
		}, keyHolder);

		student.studentId = keyHolder.getKey().longValue();
	}
}
