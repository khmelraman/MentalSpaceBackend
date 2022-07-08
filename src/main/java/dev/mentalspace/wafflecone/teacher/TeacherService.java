package dev.mentalspace.wafflecone.teacher;

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
public class TeacherService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Teacher getById(long id) {
		String sql = "SELECT teacher_id, canonical_id, first_name, last_name, phone, department FROM teacher WHERE teacher_id = ?;";
		RowMapper<Teacher> rowMapper = new TeacherRowMapper();
		Teacher teacher = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return teacher;
	}

	public boolean existsById(long id) {
		String sql = "SELECT COUNT(*) FROM teacher WHERE teacher_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}

	public void updateTeacher(Teacher teacher) {
		String sql = "UPDATE teacher SET canonical_id = ?, first_name = ?, last_name = ?, phone = ?, department = ? WHERE teacher_id = ?;";
		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, teacher.canonicalId);
				ps.setString(2, teacher.firstName);
				ps.setString(3, teacher.lastName);
				ps.setLong(4, teacher.phone);
				ps.setString(5, teacher.department);
				ps.setLong(6, teacher.teacherId);
				return ps;
			}
		});
	}

	public void add(Teacher teacher) {
		String sql = "INSERT INTO teacher (canonical_id, first_name, last_name, phone, department)"
				+ " VALUES (?, ?, ?, ?, ?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, teacher.canonicalId);
				ps.setString(2, teacher.firstName);
				ps.setString(3, teacher.lastName);
				ps.setLong(4, teacher.phone);
				ps.setString(5, teacher.department);
				return ps;
			}
		}, keyHolder);

		teacher.teacherId = keyHolder.getKey().longValue();
	}
}
