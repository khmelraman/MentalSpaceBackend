package dev.mentalspace.wafflecone.assignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AssignmentRowMapper implements RowMapper<Assignment> {
    @Override
    public Assignment mapRow(ResultSet row, int rowNum) throws SQLException {
        Assignment assignment = new Assignment();
        assignment.assignmentId = row.getLong("assignment_id");
        assignment.periodId = row.getLong("period_id");
        assignment.dateAssigned = row.getLong("date_assigned");
        assignment.dateDue = row.getLong("date_due");
        assignment.type = row.getString("type");
        assignment.estimatedBurden = row.getLong("estimated_burden");
        assignment.name = row.getString("name");
        assignment.description = row.getString("description");
        return assignment;
    }
}
