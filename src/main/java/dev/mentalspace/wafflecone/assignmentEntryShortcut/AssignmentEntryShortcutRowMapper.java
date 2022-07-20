package dev.mentalspace.wafflecone.assignmentEntryShortcut;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class AssignmentEntryShortcutRowMapper implements RowMapper<AssignmentEntryShortcut> {
    @Override
    public AssignmentEntryShortcut mapRow(ResultSet row, int rowNum) throws SQLException {
        AssignmentEntryShortcut assignmentEntryShortcut = new AssignmentEntryShortcut();
        assignmentEntryShortcut.assignmentEntryShortcutId   = row.getLong  ("assignment_entry_shortcut_id");
        assignmentEntryShortcut.teacherId                   = row.getLong  ("teacher_id");
        assignmentEntryShortcut.value                       = row.getString("value");
        return assignmentEntryShortcut;
    }
}
