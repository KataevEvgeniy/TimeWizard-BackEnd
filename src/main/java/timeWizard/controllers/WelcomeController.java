package timeWizard.controllers;

import java.sql.SQLDataException;
import java.util.*;

import javax.crypto.BadPaddingException;



import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import timeWizard.TableTask;
import timeWizard.User;
import timeWizard.CalendarTask;
import timeWizard.DAOLayer.MainDao;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;



@RestController
@RequestMapping(produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class WelcomeController {


	MainDao dao = new MainDao();
	public HttpHeaders createAuthHeaders(EncryptedAuthToken token){
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token.toString());
		headers.add("Access-Control-Expose-Headers", "Authorization");
		return headers;
	}
	
	@PostMapping( path="/register", consumes ={"application/json"})
	public ResponseEntity<String> register(@RequestBody User user) {
		if(user == null) {
			return new ResponseEntity<>("User may not have been initialized", HttpStatus.BAD_REQUEST);
		}
		user.encryptPassword();

		try {
			dao.create(user);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("User already registered", HttpStatus.BAD_REQUEST);
		}
		
		AuthToken token = new AuthToken(user);
		EncryptedAuthToken encryptedToken = token.encrypt();
		HttpHeaders headers = createAuthHeaders(encryptedToken);

		return new ResponseEntity<>("Login is accept", headers, HttpStatus.CREATED);
	}
	
	@PostMapping(path="/login", consumes ={"application/json"})
	public ResponseEntity<String> login(@RequestBody User loggingUser){
		if(loggingUser == null) {
			return new ResponseEntity<>("User may not have been initialized", HttpStatus.BAD_REQUEST);
		}
		loggingUser.encryptPassword();
		
		User existingUser = (User)dao.read(User.class, loggingUser.getEmail());
		if(!loggingUser.equals(existingUser)) {
			return new ResponseEntity<>("Login failed", HttpStatus.BAD_REQUEST);
		}
		
		AuthToken token = new AuthToken(loggingUser);
		EncryptedAuthToken encryptedToken = token.encrypt();
		HttpHeaders headers = createAuthHeaders(encryptedToken);

		return new ResponseEntity<>("Login is accept", headers, HttpStatus.ACCEPTED);
	}
	
	@GetMapping(path="/checkToken")
	public ResponseEntity<String> checkToken(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		try {
			if(encryptedToken.isTrue()) {
				return new ResponseEntity<>("Token is true", HttpStatus.ACCEPTED);
			}
		} catch (BadPaddingException e) {
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>("Token is false", HttpStatus.BAD_REQUEST);
	}



	@PostMapping(path="/saveCalendarTask", consumes ={"application/json"})
	public ResponseEntity<String> saveCalendarTask(@RequestBody CalendarTask task, @RequestHeader(name = "Authorization") String token) {
		String userEmail = getUserEmail(token);
		if(userEmail == null){
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		task.setEmail(userEmail);
		try {
			dao.create(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't created", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Task created", HttpStatus.CREATED);
	}
	
	@PostMapping(path="/updateCalendarTask", consumes ={"application/json"})
	public ResponseEntity<String> updateCalendarTask(@RequestBody CalendarTask task,@RequestHeader(name = "Authorization") String token) {
		String userEmail = getUserEmail(token);
		if(userEmail == null){
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		task.setEmail(userEmail);
		try {
			dao.update(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't update", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Task updated", HttpStatus.CREATED);
	}

	@PostMapping(path="/deleteCalendarTask", consumes ={"application/json"})
	public ResponseEntity<String> deleteCalendarTask(@RequestBody CalendarTask task,@RequestHeader(name = "Authorization") String token){
		String userEmail = getUserEmail(token);
		if(userEmail == null){
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		task.setEmail(userEmail);
		try {
			dao.delete(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't delete", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Task deleted", HttpStatus.CREATED);
	}

	@PostMapping(path="/saveTableTask", consumes ={"application/json"})
	public ResponseEntity<String> saveTableTask(@RequestBody TableTask task, @RequestHeader(name = "Authorization") String token) {
		String userEmail = getUserEmail(token);
		if(userEmail == null){
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		task.setEmail(userEmail);

		try {
			dao.create(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't created", HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<>("Task created", headers, HttpStatus.CREATED);
	}

	private String getUserEmail(String token){
		try {
			EncryptedAuthToken encryptedAuthToken = new EncryptedAuthToken(token);
			AuthToken decryptedToken = encryptedAuthToken.decrypt();
			return decryptedToken.getUserEmail();
		} catch (BadPaddingException e) {
			return null;
		}
	}

	@GetMapping(path="/getAllCallendarTasks")
	public ResponseEntity<?> getAllCalendarTasks(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		String email;

		try {
			if(!encryptedToken.isTrue()) {
				return new ResponseEntity<>("Token is false", HttpStatus.BAD_REQUEST);
			}
			email = encryptedToken.decrypt().getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		@SuppressWarnings (value="unchecked")
		ArrayList<CalendarTask> list = (ArrayList<CalendarTask>) dao.readAll(CalendarTask.class,email);

		return new ResponseEntity<>(list,HttpStatus.ACCEPTED);
	}

	@GetMapping(path="/getAllTableTasks")
	public ResponseEntity<?> getAllTableTasks(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		String email;

		try {
			if(!encryptedToken.isTrue()) {
				return new ResponseEntity<>("Token is false", HttpStatus.BAD_REQUEST);
			}
			email = encryptedToken.decrypt().getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		@SuppressWarnings (value="unchecked")
		ArrayList<CalendarTask> tableTasks = (ArrayList<CalendarTask>) dao.readAll(TableTask.class,email);

		return new ResponseEntity<>(tableTasks,HttpStatus.ACCEPTED);
	}
}
