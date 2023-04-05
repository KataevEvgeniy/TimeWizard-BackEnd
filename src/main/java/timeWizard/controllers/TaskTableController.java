package timeWizard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import timeWizard.DAOLayer.Dao;
import timeWizard.DAOLayer.MainDao;
import timeWizard.entity.TableColumn;
import timeWizard.entity.TableTask;
import timeWizard.tokens.EncryptedAuthToken;

import javax.crypto.BadPaddingException;
import java.sql.SQLDataException;
import java.util.ArrayList;

@RestController
@RequestMapping(produces = "application/json")
public class TaskTableController extends AbstractController {

    @Autowired
    public TaskTableController(Dao dao) {
        super(dao);
    }

    @PostMapping(path="/saveTableTask", consumes ={"application/json"})
    public ResponseEntity<String> saveTableTask(@RequestBody TableTask task, @RequestHeader(name = "Authorization") String token) {

        try {
            dao.create(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't create", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Task created", headers, HttpStatus.CREATED);
    }

    @PostMapping(path="/updateTableTask", consumes ={"application/json"})
    public ResponseEntity<String> updateTableTask(@RequestBody TableTask task, @RequestHeader(name = "Authorization") String token) {

        try {
            dao.update(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't update", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Task updated", headers, HttpStatus.CREATED);
    }

    @PostMapping(path="/deleteTableTask", consumes ={"application/json"})
    public ResponseEntity<String> deleteTableTask(@RequestBody TableTask task, @RequestHeader(name = "Authorization") String token) {

        try {
            dao.delete(task);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Task didn't delete", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Task deleted", headers, HttpStatus.CREATED);
    }

    @PostMapping(path="/getAllTableTasks", consumes ={"application/json"})
    public ResponseEntity<?> getAllTableTasks(@RequestBody TableColumn column, @RequestHeader(name = "Authorization") String token) {

        @SuppressWarnings (value="unchecked")
        ArrayList<TableTask> tableTasks = (ArrayList<TableTask>) dao.readAll(TableTask.class,column);

        return new ResponseEntity<>(tableTasks,HttpStatus.ACCEPTED);
    }

    @PostMapping(path="/saveTableColumn", consumes ={"application/json"})
    public ResponseEntity<String> saveTableColumn(@RequestBody TableColumn column, @RequestHeader(name = "Authorization") String token) {
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }
        column.setEmail(userEmail);

        try {
            dao.create(column);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Column didn't create", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Column created", headers, HttpStatus.CREATED);
    }

    @PostMapping(path="/updateTableColumn", consumes ={"application/json"})
    public ResponseEntity<String> updateTableColumn(@RequestBody TableColumn column, @RequestHeader(name = "Authorization") String token) {
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }
        column.setEmail(userEmail);

        try {
            dao.update(column);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Column didn't update", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Column updated", headers, HttpStatus.CREATED);
    }



    @PostMapping(path="/deleteTableColumn", consumes ={"application/json"})
    public ResponseEntity<String> deleteTableColumn(@RequestBody TableColumn column, @RequestHeader(name = "Authorization") String token) {
        String userEmail;
        try {
            userEmail = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }
        column.setEmail(userEmail);

        try {
            dao.delete(column);
        } catch (SQLDataException e) {
            return new ResponseEntity<>("Column didn't delete", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<>("Column deleted", headers, HttpStatus.CREATED);
    }



    @GetMapping(path="/getAllTableColumns")
    public ResponseEntity<?> getAllTableColumns(@RequestHeader(name = "Authorization") String token) {
        String email;

        try {
            email = getUserEmail(token);
        } catch (BadPaddingException e) {
            return new ResponseEntity<>("Token is expired", HttpStatus.UNAUTHORIZED);
        }

        @SuppressWarnings (value="unchecked")
        ArrayList<TableColumn> tableColumns = (ArrayList<TableColumn>) dao.readAll(TableColumn.class,email);

        return new ResponseEntity<>(tableColumns,HttpStatus.ACCEPTED);
    }
}
