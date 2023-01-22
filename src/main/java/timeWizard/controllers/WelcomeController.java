package timeWizard.controllers;

import java.io.IOException;
import java.io.StringReader;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import timeWizard.User;
import timeWizard.UserTask;
import timeWizard.DAOLayer.MainDAO;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;



@RestController
@RequestMapping(produces = "application/json")
@CrossOrigin(origins="http://localhost:8080")
public class WelcomeController {
	
	@PostMapping( path="/register", consumes ={"application/json"})
	public ResponseEntity<String> register(@RequestBody String userRegisterData) {
		HttpHeaders headers = new HttpHeaders();
		User user = null;
		try {
			ObjectMapper mapper = new ObjectMapper(); //Deserialization request JSON
			user = mapper.readValue((new StringReader(userRegisterData)), User.class);
		} catch (IOException e) {e.printStackTrace();}
		
		if(user == null) return new ResponseEntity<String>("app.User may not have been initialized", HttpStatus.BAD_REQUEST);
		
		user.encryptPassword();

		try {
			MainDAO.create(user);
		} catch (SQLDataException e) {
			return new ResponseEntity<String>("app.User already registered", headers, HttpStatus.BAD_REQUEST);
		}
		
		AuthToken token = new AuthToken(user);
		EncryptedAuthToken encryptedToken = token.encrypt();
		
	    headers.add("Authorization", encryptedToken.toString());
	    headers.add("Access-Control-Expose-Headers", "Authorization");
		return new ResponseEntity<String>("Login is accept", headers, HttpStatus.CREATED);
	}
	
	@PostMapping(path="/login", consumes ={"application/json"})
	public ResponseEntity<String> login(@RequestBody String userLoginData){
		HttpHeaders headers = new HttpHeaders();
		User loginingUser = null;
		
		try {
			ObjectMapper mapper = new ObjectMapper(); //Deserialization requested JSON
			loginingUser = mapper.readValue((new StringReader(userLoginData)), User.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(loginingUser == null) return new ResponseEntity<String>("app.User may not have been initialized", HttpStatus.BAD_REQUEST);
		loginingUser.encryptPassword();
		
		User verifyUser = (User)MainDAO.read(User.class, loginingUser.getEmail());
		if(!loginingUser.equals(verifyUser)) {
			return new ResponseEntity<String>("Login failed", headers, HttpStatus.BAD_REQUEST);
		}
		
		AuthToken token = new AuthToken(loginingUser);
		EncryptedAuthToken encryptedToken = token.encrypt();
		
	    headers.add("Authorization", encryptedToken.getEncryptedStringToken());
	    headers.add("Access-Control-Expose-Headers", "Authorization");
		return new ResponseEntity<String>("Login is accept", headers, HttpStatus.ACCEPTED);
	}
	
	@GetMapping(path="/checkToken")
	public ResponseEntity<String> checkToken(@RequestHeader(name = "Authorization") String token) {
		EncryptedAuthToken encryptedToken = new EncryptedAuthToken(token);
		try {
			if(encryptedToken.isTrue()) {
				return new ResponseEntity<String>("Token is true", HttpStatus.ACCEPTED);
			}
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>("Token is false", HttpStatus.BAD_REQUEST);
	}
	
	@PostMapping(path="/saveTask", consumes ={"application/json"})
	public ResponseEntity<String> saveTask(@RequestBody String taskData,@RequestHeader(name = "Authorization") String token) {
		UserTask task = new UserTask();
		String userEmail = "";
		
		try { 
			AuthToken decryptedToken = (new EncryptedAuthToken(token)).decrypt();
			userEmail = decryptedToken.getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		System.out.println(1);
		
		try {
			ObjectMapper mapper = new ObjectMapper(); //Deserialization requested JSON
			task = mapper.readValue((new StringReader(taskData)), UserTask.class);
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
	
	@PostMapping(path="/updateTask", consumes ={"application/json"})
	public ResponseEntity<String> updateTask(@RequestBody String taskData,@RequestHeader(name = "Authorization") String token) {
		UserTask task = new UserTask();
		String userEmail = "";
		
		try { 
			AuthToken decryptedToken = (new EncryptedAuthToken(token)).decrypt();
			userEmail = decryptedToken.getUserEmail();
		} catch (BadPaddingException e) {
			return new ResponseEntity<String>("Token is expired", HttpStatus.BAD_REQUEST);
		}
		System.out.println(1);
		
		try {
			ObjectMapper mapper = new ObjectMapper(); //Deserialization requested JSON
			task = mapper.readValue((new StringReader(taskData)), UserTask.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		task.setEmail(userEmail);
		try {
			MainDAO.update(task);
		} catch (SQLDataException e) {
			return new ResponseEntity<String>("Task didn't update", HttpStatus.BAD_REQUEST);
		}
		
		HttpHeaders headers = new HttpHeaders();
		return new ResponseEntity<String>("Task updated", headers, HttpStatus.CREATED);
	}
	
	@GetMapping(path="/getAllTasks")
	public ResponseEntity<String> getAllTasks(@RequestHeader(name = "Authorization") String token) {
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
		ArrayList<UserTask> list = (ArrayList<UserTask>) MainDAO.readAll(UserTask.class,email);
		
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
