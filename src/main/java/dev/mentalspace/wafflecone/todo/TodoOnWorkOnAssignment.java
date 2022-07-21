package dev.mentalspace.wafflecone.todo;

import org.json.JSONObject;

public class TodoOnWorkOnAssignment {
    public Long todoId;
    public Long workId;
    public Long date;
    public Long plannedTime;
    public Long projectedStartTime;
    public Integer todoPriority;
    public Long studentId;
    public Long assignmentId;
    public Long remainingTime;
    public Integer workPriority;
    public Long periodId;
    public Long dateAssigned;
    public Long dateDue;
    public String type;
    public Long estimatedBurden;
    public String name;
    public String description;
    public Integer points;

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("todoId",              this.todoId)
            .put("workId",              this.workId)
            .put("date",                this.date)
            .put("plannedTime",         this.plannedTime)
            .put("projectedStartTime",  this.projectedStartTime)
            .put("todoPriority",        this.todoPriority)
            .put("studentId",           this.studentId)
            .put("assignmentId",        this.assignmentId)
            .put("remainingTime",       this.remainingTime)
            .put("workPriority",        this.workPriority)
            .put("periodId",            this.periodId)
            .put("dateAssigned",        this.dateAssigned)
            .put("dateDue",             this.dateDue)
            .put("type",                this.type)
            .put("estimatedBurden",     this.estimatedBurden)
            .put("name",                this.name)
            .put("description",         this.description)
            .put("points",              this.points);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("todoId",              this.todoId)
            .put("workId",              this.workId)
            .put("date",                this.date)
            .put("plannedTime",         this.plannedTime)
            .put("projectedStartTime",  this.projectedStartTime)
            .put("todoPriority",        this.todoPriority)
            .put("studentId",           this.studentId)
            .put("assignmentId",        this.assignmentId)
            .put("remainingTime",       this.remainingTime)
            .put("workPriority",        this.workPriority)
            .put("periodId",            this.periodId)
            .put("dateAssigned",        this.dateAssigned)
            .put("dateDue",             this.dateDue)
            .put("type",                this.type)
            .put("estimatedBurden",     this.estimatedBurden)
            .put("name",                this.name)
            .put("description",         this.description)
            .put("points",              this.points);
        return jsonObject;
    }
}
