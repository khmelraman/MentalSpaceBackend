package dev.mentalspace.wafflecone.period;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PeriodRowMapper implements RowMapper<Period> {
    @Override
    public Period mapRow(ResultSet row, int rowNum) throws SQLException {
        Period period = new Period();
        period.periodId = row.getLong("period_id");
        period.teacherId = row.getLong("teacher_id");
        period.subjectId = row.getLong("subject_id");
        period.period = row.getInt("period");
        period.classCode = row.getString("class_code");
        period.archived = row.getBoolean("archived");
        return period;
    }
}
