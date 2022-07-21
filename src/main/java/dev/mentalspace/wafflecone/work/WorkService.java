package dev.mentalspace.wafflecone.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public List<Work> getByIdList(List<Long> id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE work_id IN (:ids) ORDER BY remaining_time DESC;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        Map idsMap = Collections.singletonMap("ids", id);
        List<Work> works = jdbcTemplate.query(sql, rowMapper, idsMap);
        return works;
    }

    public List<Work> getByTodoIdList(List<Long> id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE todo_id IN (:ids) ORDER BY remaining_time DESC;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        Map idsMap = Collections.singletonMap("ids", id);
        List<Work> todos = jdbcTemplate.query(sql, rowMapper, idsMap);
        return todos;
    }

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
                + "WHERE student_id = ? ORDER BY 1;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        List<Work> works = jdbcTemplate.query(sql, rowMapper, id);
        return works;
    }

    public List<Work> getByStudentId(long id, boolean outstanding) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE student_id = ?" + 
                (outstanding ? " AND remaining_time = 0" : "") + " ORDER BY 1;";
        RowMapper<Work> rowMapper = new WorkRowMapper();
        List<Work> works = jdbcTemplate.query(sql, rowMapper, id);
        return works;
    }

    public List<Work> getByAssignmentId(long id) {
        String sql = "SELECT work_id, student_id, assignment_id, remaining_time, priority FROM work "
                + "WHERE assignment_id = ? ORDER BY 1;";
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
                + "WHERE work_id = ?;";
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

    public void updateWorkRemainingTime(long id, long time) {
        String sql = "UPDATE work SET remaining_time = ? "
                + "WHERE work_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, time);
                ps.setLong(2, id);
                return ps;
            }
        });
    }

    public void updateWorkRemainingTimeByIncreasing(long id, long time) {
        String sql = "UPDATE work SET remaining_time = ((SELECT remaining_time FROM work WHERE work_id = ?) + ?)) "
                + "WHERE work_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, id);
                ps.setLong(2, time);
                ps.setLong(2, id);
                return ps;
            }
        });
    }

    public void updateWorkRemainingTimeUponTodoDeletion(long workId, long todoId) {
        String sql = "UPDATE work SET remaining_time = ((SELECT remaining_time FROM work WHERE work_id = ?) + (SELECT planned_time FROM todo WHERE todo_id = ?)))) "
                + "WHERE work_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, workId);
                ps.setLong(2, todoId);
                ps.setLong(2, workId);
                return ps;
            }
        });
    }

    public void updateWorkRemainingTimeUponTodoAddition(long workId, long todoId) {
        String sql = "UPDATE work SET remaining_time = ((SELECT remaining_time FROM work WHERE work_id = ?) - (SELECT planned_time FROM todo WHERE todo_id = ?)))) "
                + "WHERE work_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, workId);
                ps.setLong(2, todoId);
                ps.setLong(2, workId);
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