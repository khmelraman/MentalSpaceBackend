package dev.mentalspace.wafflecone.assignmentType;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AssignmentTypeRowMapper implements RowMapper<AssignmentType> {
    @Override
    public AssignmentType mapRow(ResultSet row, int rowNum) throws SQLException {
        AssignmentType assignmentEntryShortcut = new AssignmentType();
        assignmentEntryShortcut.assignmentEntryShortcutId   = row.getLong  ("assignment_type_id");
        assignmentEntryShortcut.teacherId                   = row.getLong  ("teacher_id");
        assignmentEntryShortcut.value                       = row.getString("value");
        return assignmentEntryShortcut;
    }
}
