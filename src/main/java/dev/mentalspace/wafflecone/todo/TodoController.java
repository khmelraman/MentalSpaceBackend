package dev.mentalspace.wafflecone.todo;

import dev.mentalspace.wafflecone.Scheduler;
import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;
import dev.mentalspace.wafflecone.databaseobject.Preference;
import dev.mentalspace.wafflecone.databaseobject.PreferenceService;
import dev.mentalspace.wafflecone.databaseobject.StartType;
import dev.mentalspace.wafflecone.event.Event;
import dev.mentalspace.wafflecone.event.EventController;
import dev.mentalspace.wafflecone.event.EventService;

import java.util.List;

import org.json.JSONObject;
import org.slf4j.event.EventConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.mentalspace.wafflecone.response.ErrorResponse;
import dev.mentalspace.wafflecone.response.ErrorString;
import dev.mentalspace.wafflecone.response.Response;
import dev.mentalspace.wafflecone.user.User;
import dev.mentalspace.wafflecone.user.UserService;
import dev.mentalspace.wafflecone.user.UserType;
import dev.mentalspace.wafflecone.work.WorkService;

@RestController
@RequestMapping(path = { "/api/v0/todo" })
public class TodoController {
    @Autowired
	UserService userService;
	@Autowired
	AuthTokenService authTokenService;
    @Autowired
    TodoService todoService;
    @Autowired
    WorkService workService;
    @Autowired
    EventService eventService;
    @Autowired
    PreferenceService preferenceService;
    
    @GetMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<String> getTodo(
		@RequestHeader("Authorization") String authApiKey, 
            @RequestParam(value = "todoId", defaultValue = "-1") Long searchTodoId) {
		AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
		if (!authToken.valid) {
			JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
		}
		User loggedInUser = userService.getById(authToken.userId);

        if (!todoService.existsById(searchTodoId)) {
            JSONObject errors = new JSONObject().put("todoId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Todo todo = todoService.getById(searchTodoId);

        if (loggedInUser.type != UserType.STUDENT || loggedInUser.studentId != workService.getById(todo.workId).studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }
	
	Response response = new Response("success").put("todo", todo.toJsonObject());
        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> addTodo(
    	@RequestHeader("Authorization") String authApiKey, 
        @RequestBody Todo todo, Preference preference) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
	if (!authToken.valid) {
		JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
	}
	User loggedInUser = userService.getById(authToken.userId);

        if (!workService.existsById(todo.workId)) {
            JSONObject errors = new JSONObject().put("workId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }

        if (loggedInUser.type != UserType.STUDENT || loggedInUser.studentId != workService.getById(todo.workId).studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        // TODO: Refactor into something clean
        if (todo.workId == null || todo.date == null || todo.priority == null) {
            JSONObject errors = new JSONObject();
            if(todo.workId   == null) {errors.put("workId",   ErrorString.emptyString("workId"));}
            if(todo.date     == null) {errors.put("date",     ErrorString.emptyString("date"));}
            if(todo.priority == null) {errors.put("priority", ErrorString.emptyString("priority"));}
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }

        todoService.addTodo(todo);
        todoService.assignPriority(loggedInUser.studentId, preference);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchTodo(
    	@RequestHeader("Authorization") String authApiKey, 
        @RequestBody Todo patchDetails) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("type", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }
        if (!todoService.existsById(patchDetails.todoId)) {
            JSONObject errors = new JSONObject().put("todoId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errors).toString());
        }
        
        Todo todo = todoService.getById(patchDetails.todoId);
        patchDetails.workId = todo.workId;
        
        if (loggedInUser.studentId != workService.getById(todo.workId).studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.PERMISSION_ERROR);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

	// TODO: migrate into Todo class as updateTodo(); and clearer logic
        
        todo.updateDetails(patchDetails);
        todoService.updateTodo(todo);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @DeleteMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> deleteTodo(
    	@RequestHeader("Authorization") String authApiKey, 
        @RequestParam(value = "todoId", defaultValue = "-1") Long deleteTodoId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("type", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        if (!todoService.existsById(deleteTodoId)) {
            JSONObject errors = new JSONObject().put("todoId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }
	
        if (loggedInUser.studentId != workService.getById(deleteTodoId).studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.PERMISSION_ERROR);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        todoService.deleteTodoById(deleteTodoId);

        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }

    @PostMapping(path = "/reschedule", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> scheduleTodo(
    	@RequestHeader("Authorization") String authApiKey, 
        @RequestBody Preference schedulePreference) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("user", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }



        Preference preference = preferenceService.getByStudentId(loggedInUser.studentId);
        List<Event> events = eventService.getByStudentId(loggedInUser.studentId);
        List<Todo> todos = todoService.getByStudentId(loggedInUser.studentId);

        Event[] eventsArray = new Event[events.size()];
        Todo[] todoArray = new Todo[todos.size()];
        events.toArray(eventsArray);
        todos.toArray(todoArray);

        if(preference.startType == StartType.AS_LATE_AS_POSSIBLE) {
            Scheduler.scheduleALAP(preference, todoArray, eventsArray, schedulePreference.start, schedulePreference.end);
        }
        else if (preference.startType == StartType.AS_SOON_AS_POSSIBLE) {
            Scheduler.scheduleASAP(preference, todoArray, eventsArray, schedulePreference.start, schedulePreference.end);
        }



        return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
    }
}
