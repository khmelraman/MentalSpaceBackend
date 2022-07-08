package dev.mentalspace.wafflecone.databaseobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class TodoService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

    public Todo getById(long id) {
        String sql = 
            "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority " + 
            "FROM todo " + 
            "WHERE todo_id = ?;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        Todo todo = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return todo;
    }

    public List<Todo> getByWorkId(long id) {
        String sql = 
            "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority " + 
            "FROM todo " + 
            "WHERE woek_id = ?;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, id);
        return todos;
    }

    public List<Todo> getByStudentId(long id) {
        String sql = 
            "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority " + 
            "FROM todo JOIN work " + 
            "ON todo.work_id = work.work_id " +
            "WHERE student_id = ?;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, id);
        return todos;
    }

    public List<Todo> getByWorkIdAndStudentId(long work, long student) {
        String sql = 
            "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority " + 
            "FROM todo JOIN work " + 
            "ON todo.work_id = work.work_id " +
            "WHERE work_id = ? AND student_id = ?;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, work, student);
        return todos;
    }

    public void addTodo(Todo todo) {
        String sql = 
            "INSERT INTO todo (work_id, date, planned_time, projected_start_time, priority) " + 
            "(?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps =
						connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
					ps.setLong      (1, todo.workId);
					ps.setLong      (2, todo.date);
					ps.setLong      (3, todo.plannedTime);
					ps.setLong      (4, todo.projectedStartTime);
					ps.setInt       (5, todo.priority);
					return ps;
                }
            }, 
            keyHolder
        );

        todo.todoId = keyHolder.getKey().longValue();
    }

    public void updateTodo(Todo todo) {
        String sql = 
            "UPDATE todo SET " + 
            "work_id = ?, date = ?, date_due = ?, planned_time = ?, projected_start_time = ?, priority = ? " + 
            "WHERE todo_id = ?;";
        jdbcTemplate.update(
            new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = 
                        connection.prepareStatement(sql);
                    ps.setLong      (1, todo.workId);
                    ps.setLong      (2, todo.date);
                    ps.setLong      (3, todo.plannedTime);
                    ps.setLong      (4, todo.projectedStartTime);
                    ps.setInt       (5, todo.priority);
                    ps.setLong      (6, todo.todoId);
                    return ps;
                }
            }
        );
    }

    public void deleteTodo(Todo todo) {
        String sql = 
            "DELETE FROM todo " + 
            "WHERE todo_id = ?;";
        jdbcTemplate.update(
            new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps =
                        connection.prepareStatement(sql);
                    ps.setLong(1, todo.todoId);
                    return ps;
                }
            }
        );
    }
}