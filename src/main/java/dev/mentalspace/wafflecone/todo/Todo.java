package dev.mentalspace.wafflecone.todo;

import org.json.JSONObject;

public class Todo {
    public Long todoId;
    public Long workId;
    public Long date;
    public Long plannedTime;
    public Long projectedStartTime;
    public Integer priority;

    public Todo() {
    }

    public void updateDetails(Todo updTodo) {
        if (updTodo.date != null) {
            this.date = updTodo.date;
        }
        if (updTodo.plannedTime != null) {
            this.plannedTime = updTodo.plannedTime;
        }
        if (updTodo.projectedStartTime != null) {
            this.projectedStartTime = updTodo.projectedStartTime;
        }
        if (updTodo.priority != null) {
            this.priority = updTodo.priority;
        }
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject
            .put("todoId",                this.todoId)
            .put("workId",                this.workId)
            .put("date",                  this.date)
            .put("plannedTime",           this.plannedTime)
            .put("projectedStartTime",    this.projectedStartTime)
            .put("priority",              this.priority);
        return jsonObject;
    }

    public JSONObject toJsonObject(JSONObject jsonObject) {
        jsonObject
            .put("todoId",                this.todoId)
            .put("workId",                this.workId)
            .put("date",                  this.date)
            .put("plannedTime",           this.plannedTime)
            .put("projectedStartTime",    this.projectedStartTime)
            .put("priority",              this.priority);
        return jsonObject;
    }
}
