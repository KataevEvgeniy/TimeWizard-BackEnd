package timeWizard;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.DatatypeConverter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonAutoDetect
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
public class User {
	
	private String username;
	@Id
	private String email;
	private String password;
	
	public void encryptPassword() {
		try { 
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] digest = messageDigest.digest(this.password.getBytes("UTF-8"));
			this.password = DatatypeConverter.printHexBinary(digest).toLowerCase();
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		return this.username + "||" + this.email + "||" + this.password;
	}
	
	public boolean equals(User user) {
		if (user.getEmail().equals(this.email) && user.getPassword().equals(this.password)) {
			return true;
		}
		return false;
	}
	
}
