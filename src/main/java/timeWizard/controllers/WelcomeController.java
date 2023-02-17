package timeWizard.controllers;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLDataException;
import java.util.*;

import javax.crypto.BadPaddingException;


import com.fasterxml.jackson.core.JsonProcessingException;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import timeWizard.TableTask;
import timeWizard.User;
import timeWizard.CalendarTask;
import timeWizard.DAOLayer.MainDAO;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;



@RestController
@RequestMapping(produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class WelcomeController {

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
			MainDAO.create(user);
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
		
		User existingUser = (User)MainDAO.read(User.class, loggingUser.getEmail());
		if(!loggingUser.equals(existingUser)) {
			return new ResponseEntity<String>("Login failed", HttpStatus.BAD_REQUEST);
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
		String userEmail = "";

		try {
			AuthToken decryptedToken = (new EncryptedAuthToken(token)).decrypt();
			userEmail = decryptedToken.getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		task.setEmail(userEmail);
		try {
			MainDAO.create(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't created", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Task created", HttpStatus.CREATED);
	}

	private String getUserEmail(String token){
		try {
			EncryptedAuthToken encryptedAuthToken = new EncryptedAuthToken(token);
			AuthToken decryptedToken = encryptedAuthToken.decrypt();
			String userEmail = decryptedToken.getUserEmail();
			return userEmail;
		} catch (BadPaddingException e) {
			return null;
		}

	}
	
	@PostMapping(path="/updateCalendarTask", consumes ={"application/json"})
	public ResponseEntity<String> updateCalendarTask(@RequestBody CalendarTask task,@RequestHeader(name = "Authorization") String token) {
		String userEmail = getUserEmail(token);
		if(userEmail == null){
			return new ResponseEntity<>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		task.setEmail(userEmail);
		try {
			MainDAO.update(task);
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
			MainDAO.delete(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<>("Task didn't delete", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("Task deleted", HttpStatus.CREATED);
	}
	
	@GetMapping(path="/getAllCallendarTasks")
	public ResponseEntity<String> getAllCalendarTasks(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		String email;
		String JSONList = "";
		
		try {
			if(!encryptedToken.isTrue()) {
				return new ResponseEntity<String>("Token is false", HttpStatus.BAD_REQUEST);
			}
			email = encryptedToken.decrypt().getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		
		@SuppressWarnings (value="unchecked")
		ArrayList<CalendarTask> list = (ArrayList<CalendarTask>) MainDAO.readAll(CalendarTask.class,email);
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONList = mapper.writeValueAsString(list);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<String>(JSONList,HttpStatus.ACCEPTED);
	}

	@PostMapping(path="/saveTableTask", consumes ={"application/json"})
	public ResponseEntity<String> saveTableTask(@RequestBody String taskData, @RequestHeader(name = "Authorization") String token) {
		TableTask task = new TableTask();
		String userEmail = "";

		try {
			AuthToken decryptedToken = (new EncryptedAuthToken(token)).decrypt();
			userEmail = decryptedToken.getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}


		try {
			ObjectMapper mapper = new ObjectMapper(); //Deserialization requested JSON
			task = mapper.readValue((new StringReader(taskData)), TableTask.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		task.setEmail(userEmail);
		try {
			MainDAO.create(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<String>("Task didn't created", HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>("Task created", headers, HttpStatus.CREATED);
	}

	@GetMapping(path="/getAllTableTasks")
	public ResponseEntity<String> getAllTableTasks(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		String email;
		String JSONList = "";

		try {
			if(!encryptedToken.isTrue()) {
				return new ResponseEntity<String>("Token is false", HttpStatus.BAD_REQUEST);
			}
			email = encryptedToken.decrypt().getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}

		@SuppressWarnings (value="unchecked")
		ArrayList<CalendarTask> list = (ArrayList<CalendarTask>) MainDAO.readAll(TableTask.class,email);

		try {
			ObjectMapper mapper = new ObjectMapper();
			JSONList = mapper.writeValueAsString(list);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<String>(JSONList,HttpStatus.ACCEPTED);
	}
}
