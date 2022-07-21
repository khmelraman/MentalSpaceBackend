package dev.mentalspace.wafflecone.todo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.mentalspace.wafflecone.databaseobject.AssignmentOrder;
import dev.mentalspace.wafflecone.databaseobject.Preference;
import dev.mentalspace.wafflecone.work.*;

@Transactional
@Repository
public class TodoService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    WorkService workService;

    public List<Todo> getByIdList(List<Long> id) {
        String sql = "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority FROM todo "
                + "WHERE todo_id IN (:ids) ORDER BY priority;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        Map idsMap = Collections.singletonMap("ids", id);
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, idsMap);
        return todos;
    }

    public boolean existsById(Long id) {
		String sql = "SELECT COUNT(*) FROM todo WHERE todo_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
		return count != 0;
	}

    public boolean existsByIdAndWorkId(Long t_id, long w_id) {
		String sql = "SELECT COUNT(*) FROM todo WHERE todo_id = ? AND work_id = ?;";
		int count = jdbcTemplate.queryForObject(sql, Integer.class, t_id, w_id);
		return count != 0;
	}

    public Todo getById(long id) {
        String sql = "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority FROM todo "
                + "WHERE todo_id = ?;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        Todo todo = jdbcTemplate.queryForObject(sql, rowMapper, id);
        return todo;
    }

    public List<Todo> getByWorkId(long id) {
        String sql = "SELECT todo_id, work_id, date, planned_time, projected_start_time, priority FROM todo "
                + "WHERE work_id = ? ORDER BY priority;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, id);
        return todos;
    }

    public List<Todo> getByStudentId(long id) {
        String sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY priority;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, id);
        return todos;
    }

    // Ensure whatever uses this function has default values; THIS IS NULL-UNSAFE CODE
    // TODO: REFACTOR THIS SO IT'S NULL-SAFE JUST IN CASE:tm:
    public List<Todo> getByStudentId(long id, long start, long end) {
        String sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY priority" 
                + (start <= 0 ? "" : " AND date >= " + String.valueOf(start))
                + (end <= 0 ? "" : " AND date <= " + String.valueOf(end)) + ";";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, id);
        return todos;
    }

    public List<Todo> getByWorkIdAndStudentId(long work, long student) {
        String sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE work_id = ? AND student_id = ? ORDER BY priority;";
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        List<Todo> todos = jdbcTemplate.query(sql, rowMapper, work, student);
        return todos;
    }

    public void addTodo(Todo todo) {
        String sql = "INSERT INTO todo (work_id, date, planned_time, projected_start_time, priority) VALUES "
                + "(?, ?, ?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, todo.workId);
                ps.setLong(2, todo.date);
                ps.setLong(3, todo.plannedTime);
                ps.setLong(4, todo.projectedStartTime);
                ps.setInt(5, todo.priority);
                return ps;
            }
        }, keyHolder);

        todo.todoId = keyHolder.getKey().longValue();

        workService.updateWorkRemainingTimeUponTodoAddition(todo.workId, todo.todoId);
    }

    public void updateTodo(Todo todo) {
        String sql = "UPDATE todo SET "
                + "work_id = ?, date = ?, date_due = ?, planned_time = ?, projected_start_time = ?, priority = ? "
                + "WHERE todo_id = ?;";
        workService.updateWorkRemainingTimeUponTodoDeletion(todo.workId, todo.todoId);
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, todo.workId);
                ps.setLong(2, todo.date);
                ps.setLong(3, todo.plannedTime);
                ps.setLong(4, todo.projectedStartTime);
                ps.setInt(5, todo.priority);
                ps.setLong(6, todo.todoId);
                return ps;
            }
        });
        workService.updateWorkRemainingTimeUponTodoAddition(todo.workId, todo.todoId);
    }

    public void deleteTodoById(long todoId) {
        String sql = "DELETE FROM todo WHERE todo_id = ?;";
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setLong(1, todoId);
                return ps;
            }
        });
    }
    
    public void deleteTodo(Todo todo) {
    	deleteTodoById(todo.todoId);
        workService.updateWorkRemainingTimeUponTodoDeletion(todo.workId, todo.todoId);
	}

    public void assignPriority(long id, Preference preference) {
        RowMapper<Todo> rowMapper = new TodoRowMapper();
        int i = 0;
        String sql = new String();
        List<Todo> todos = getByStudentId(id);

        AssignmentOrder order = preference.assignmentOrder;
        switch(order) {
            case SHORT_JOB_FIRST:
                sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY todo.planned_time;";
                break;
            case LONG_JOB_FIRST: 
                sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY todo.planned_time DESC;";
                break;
            case IN_SUBJECTS_ORDER:
                sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY work.priority;";
                break;
            default:
                sql = "SELECT todo.todo_id, todo.work_id, todo.date, todo.planned_time, todo.projected_start_time, todo.priority "
                    + "FROM todo JOIN work ON todo.work_id = work.work_id WHERE student_id = ? ORDER BY work.priority;";
        }

        todos = jdbcTemplate.query(sql, rowMapper, id);

        for(Todo todo: todos) {
            todo.priority = i;
            updateTodo(todo);
            i++;
        }
    }
}
