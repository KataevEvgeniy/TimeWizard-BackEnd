package timeWizard.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timeWizard.DAOLayer.Dao;
import timeWizard.entity.CalendarTask;
import timeWizard.tokens.EncryptedAuthToken;

import javax.crypto.BadPaddingException;
import java.sql.SQLDataException;
import java.util.ArrayList;

@RestController
@RequestMapping(produces = "application/json")
public class CalendarController extends AbstractController{

    @Autowired
    CalendarController(Dao dao) {
        super(dao);
    }
    @PostMapping(path="/saveCalendarTask", consumes ={"application/json"})
    public ResponseEntity<?> saveCalendarTask(@RequestBody CalendarTask task, @RequestHeader(name = "Authorization") String token) {
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }

        task.setEmail(userEmail);
        try {
            dao.create(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't created", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PostMapping(path="/updateCalendarTask", consumes ={"application/json"})
    public ResponseEntity<?> updateCalendarTask(@RequestBody CalendarTask task,@RequestHeader(name = "Authorization") String token) {
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }

        task.setEmail(userEmail);
        try {
            dao.update(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't update", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(task, HttpStatus.CREATED);
    }

    @PostMapping(path="/deleteCalendarTask", consumes ={"application/json"})
    public ResponseEntity<String> deleteCalendarTask(@RequestBody CalendarTask task,@RequestHeader(name = "Authorization") String token){
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }

        task.setEmail(userEmail);
        try {
            dao.delete(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't delete", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Task deleted", HttpStatus.CREATED);
    }


    @GetMapping(path="/getAllCalendarTasks")
    public ResponseEntity<?> getAllCalendarTasks(@RequestHeader(name = "Authorization") String token) {
        String email;

        try {
            email = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }

        @SuppressWarnings (value="unchecked")
        ArrayList<CalendarTask> list = (ArrayList<CalendarTask>) dao.readAll(CalendarTask.class,email);

        return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
    }
}
