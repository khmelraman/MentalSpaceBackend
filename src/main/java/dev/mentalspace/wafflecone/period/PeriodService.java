package dev.mentalspace.wafflecone.period;

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
public class PeriodService {
    @Autowired
	private JdbcTemplate jdbcTemplate;

    public Period getById(long id) {
		String sql =
			"SELECT period_id, teacher_id, subject_id, period, class_code, archived FROM period WHERE period_id = ?;";
		RowMapper<Period> rowMapper = new PeriodRowMapper();
		Period period = jdbcTemplate.queryForObject(sql, rowMapper, id);
		return period;
	}

    public boolean existsById(long id) {
		String sql = "SELECT COUNT(*) FROM period WHERE period_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}
}
