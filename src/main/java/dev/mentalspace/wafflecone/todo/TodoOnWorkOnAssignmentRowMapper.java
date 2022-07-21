package dev.mentalspace.wafflecone.todo;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TodoOnWorkOnAssignmentRowMapper implements RowMapper<TodoOnWorkOnAssignment> {
    @Override
    public TodoOnWorkOnAssignment mapRow(ResultSet row, int rowNum) throws SQLException {
        TodoOnWorkOnAssignment todo = new TodoOnWorkOnAssignment();
        todo.todoId             = row.getLong   ("todo.todo_id");
        todo.workId             = row.getLong   ("todo.work_id");
        todo.date               = row.getLong   ("todo.date");
        todo.plannedTime        = row.getLong   ("todo.planned_time");
        todo.projectedStartTime = row.getLong   ("todo.projected_start_time");
        todo.todoPriority       = row.getInt    ("todo.priority");
        todo.studentId          = row.getLong   ("work.student_id");
        todo.assignmentId       = row.getLong   ("work.assignment_id");
        todo.remainingTime      = row.getLong   ("work.remaining_time");
        todo.workPriority       = row.getInt    ("work.priority");
        todo.periodId           = row.getLong   ("assignment.period_id");
        todo.dateAssigned       = row.getLong   ("assignment.date_assigned");
        todo.dateDue            = row.getLong   ("assignment.date_due");
        todo.type               = row.getString ("assignment.type");
        todo.estimatedBurden    = row.getLong   ("assignment.estimated_burden");
        todo.name               = row.getString ("assignment.name");
        todo.description        = row.getString ("assignment.description");
        todo.points             = row.getInt    ("assignment.points");
        return todo;
    }
}
