package dev.mentalspace.wafflecone.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.mentalspace.wafflecone.assignment.Assignment;
import dev.mentalspace.wafflecone.databaseobject.Enrollment;

@Transactional
@Repository
public class WorkService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean existsById(Long id) {
		String sql = "SELECT COUNT(*) FROM work WHERE work_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}

    public Work getById(long id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE work_id = ?;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        Work work = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return work;
    }

    public List<Work> getByStudentId(long id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE student_id = ?;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        List<Work> works = jdbcTemplate.query(sql, rowMapper, id);
        return works;
    }

    public List<Work> getByStudentId(long id, boolean outstanding) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE student_id = ?" + 
                (outstanding ? " AND remaining_time = 0" : "") + ";";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        List<Work> works = jdbcTemplate.query(sql, rowMapper, id);
        return works;
    }

    public List<Work> getByAssignmentId(long id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE assignment_id = ?;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        List<Work> works = jdbcTemplate.query(sql, rowMapper, id);
        return works;
    }

    public void addWork(Work work) {
        String sql = "INSERT INTO work (student_id, assignment_id, remaining_time, priority) VALUES (?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, work.studentId);
                ps.setLong(2, work.assignmentId);
                ps.setLong(3, work.remainingTime);
                ps.setInt(4, work.priority);
                return ps;
            }
        }, keyHolder);

        work.workId = keyHolder.getKey().longValue();
    }

    public void updateWork(Work work) {
        String sql = "UPDATE work SET student_id = ?, assignment_id = ?, remaining_time = ?, priority = ? "
                + "WHERE assignment_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, work.studentId);
                ps.setLong(2, work.assignmentId);
                ps.setLong(3, work.remainingTime);
                ps.setInt(4, work.priority);
                ps.setLong(5, work.workId);
                return ps;
            }
        });
    }

    public int[] batchAddWorkByEnrollmentsAndAssignment(List<Enrollment> enrollments, Assignment assignment) {
        // KeyHolder keyHolder = new GeneratedKeyHolder();
        int[] addCounts = jdbcTemplate.batchUpdate(
            "INSERT INTO work (student_id, assignment_id, remaining_time, priority) VALUES (?, ?, ?, ?);", 
            new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Enrollment enrollment = enrollments.get(i);
                    ps.setLong(1, enrollment.studentId);
                    ps.setLong(2, assignment.assignmentId);
                    ps.setLong(3, assignment.estimatedBurden);
                    ps.setInt (4, 0);
                }
                @Override
                public int getBatchSize() {
                    return enrollments.size();
                }
            }
        );
        return addCounts;
    }

    public void deleteWork(Work work) {
        String sql = "DELETE FROM work WHERE work_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, work.workId);
                return ps;
            }
        });
    }
}