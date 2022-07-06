package dev.mentalspace.wafflecone.databaseobject;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PreferenceRowMapper implements RowMapper<Preference> {
    @Override
    public Preference mapRow(ResultSet row, int rowNum) throws SQLException {
        Preference preference = new Preference();
        preference.preferenceId     = row.getLong ("preference_id");
        preference.studentId        = row.getLong ("student_id");
        preference.assignmentOrder  = row.getInt  ("assignment_order");
        preference.startType        = row.getInt  ("start_type");
        preference.breakLength      = row.getLong ("break_length");
        preference.breakFrequency   = row.getLong ("break_frequency");
        return preference;
    }
}
