package dev.mentalspace.wafflecone.work;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class WorkRowMapper implements RowMapper<Work> {
    @Override
    public Work mapRow(ResultSet row, int rowNum) throws SQLException {
        Work work = new Work();
        work.workId = row.getLong("work_id");
        work.studentId = row.getLong("student_id");
        work.assignmentId = row.getLong("assignment_id");
        work.remainingTime = row.getLong("remaining_time");
        work.priority = row.getInt("priority");
        return work;
    }
}
