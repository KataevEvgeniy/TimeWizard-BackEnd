package timeWizard.controllers;

import java.sql.SQLDataException;

import javax.crypto.BadPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import timeWizard.DAOLayer.Dao;
import timeWizard.entity.User;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;



@RestController
@RequestMapping(produces = "application/json")
public class AuthController extends AbstractController {

	@Autowired
	AuthController(Dao dao) {
		super(dao);
	}

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






}
