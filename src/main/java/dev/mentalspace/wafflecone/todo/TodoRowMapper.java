package dev.mentalspace.wafflecone.todo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TodoRowMapper implements RowMapper<Todo> {
    @Override
    public Todo mapRow(ResultSet row, int rowNum) throws SQLException {
        Todo todo = new Todo();
        todo.todoId = row.getLong("todo_id");
        todo.workId = row.getLong("work_id");
        todo.date = row.getLong("date");
        todo.plannedTime = row.getLong("planned_time");
        todo.projectedStartTime = row.getLong("projected_start_time");
        todo.priority = row.getInt("priority");
        return todo;
    }
}
