package dev.mentalspace.wafflecone.event;

import dev.mentalspace.wafflecone.Utils;
import dev.mentalspace.wafflecone.WaffleConeController;
import dev.mentalspace.wafflecone.auth.AuthToken;
import dev.mentalspace.wafflecone.auth.AuthScope;
import dev.mentalspace.wafflecone.auth.AuthTokenService;

import org.json.JSONObject;
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
import dev.mentalspace.wafflecone.user.*;
import dev.mentalspace.wafflecone.work.*;
import dev.mentalspace.wafflecone.event.*;

@RestController
@RequestMapping(path = { "/api/v0/event" })
public class EventController {
    @Autowired
    UserService userService;
    @Autowired
    EventService eventService;
    @Autowired
    AuthTokenService authTokenService;

    @GetMapping(path = "")
    public ResponseEntity<String> getEvent(
        @RequestHeader("Authorization") String authApiKey, 
        @RequestParam(value = "eventId", defaultValue = "-1") Long searchEventId) {
        AuthToken authToken = authTokenService.verifyBearerKey(authApiKey);
        if (!authToken.valid) {
            JSONObject errors = new JSONObject().put("accessToken", ErrorString.INVALID_ACCESS_TOKEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(errors).toString());
        }
        User loggedInUser = userService.getById(authToken.userId);

        if (loggedInUser.type != UserType.STUDENT) {
            JSONObject errors = new JSONObject().put("userType", ErrorString.USER_TYPE);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        if (!eventService.existsById(searchEventId)) {
            JSONObject errors = new JSONObject().put("eventId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
        }

        Event event = eventService.getById(searchEventId);

        if (event.studentId != loggedInUser.studentId) {
            JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
        }

        Response response = new Response("success").put("event", event.toJsonObject());

        return ResponseEntity.status(HttpStatus.OK).body(response.toString());
    }

    @PostMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> addEvent (
        @RequestHeader("Authorization") String authApiKey, 
        @RequestBody Event event) {
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

            if (event.studentId != loggedInUser.studentId) {
                JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }

            if (event.rruleString.substring(0, 6) == "RRULE:") {
                event.rruleString = event.rruleString.substring(6);
            }
            
            eventService.addEvent(event);

            return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
        }

    @PatchMapping(path = "", consumes = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<String> patchEvent (
        @RequestHeader("Authorization") String authApiKey, 
        @RequestBody Event event) {
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

            if (!eventService.existsById(event.eventId)) {
                JSONObject errors = new JSONObject().put("eventId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
            }

            Event dbEvent = eventService.getById(event.eventId);
            event.studentId = dbEvent.studentId;

            if (event.studentId != loggedInUser.studentId) {
                JSONObject errors = new JSONObject().put("studentId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }

            if (event.rruleString == null) {
                event.rruleString = dbEvent.rruleString;
            } else if (event.rruleString.substring(0, 6) == "RRULE:") {
                event.rruleString = event.rruleString.substring(6);
            }
            if (event.name == null) {
                event.name = dbEvent.name;
            }
            if (event.description == null) {
                event.description = dbEvent.description;
            }
            if (event.duration == null) {
                event.duration = dbEvent.duration;
            }

            eventService.updateEvent(event);

            return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
        }

    @DeleteMapping(path = {""})
    public ResponseEntity<String> deleteEvent(
        @RequestHeader("Authorization") String authApiKey, 
        @RequestParam(value = "eventId", defaultValue = "-1") Long deleteEventId) {
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
    
            if (!eventService.existsById(deleteEventId)) {
                JSONObject errors = new JSONObject().put("eventId", ErrorString.INVALID_ID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(errors).toString());
            }
    
            Event event = eventService.getById(deleteEventId);
    
            if (loggedInUser.studentId != event.studentId) {
                JSONObject errors = new JSONObject().put("studentId", ErrorString.PERMISSION_ERROR);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errors).toString());
            }
    
            eventService.deleteEvent(event);
    
            return ResponseEntity.status(HttpStatus.OK).body(new Response("success").toString());
        }
}