package timeWizard.tokens;



import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import lombok.Getter;
import timeWizard.entity.User;

@Getter
public class AuthToken {
	private String token;
	private String userEmail;
	private String key;
	
	public AuthToken(User user){
		this.userEmail = user.getEmail();
		this.key = DatatypeConverter.printHexBinary(AutoUpdatingKey.getKey().getEncoded());
		this.token = this.key + " " + this.userEmail;
	}
	
	public AuthToken(String token){
		this.token = token;
		String[] arrayToken = token.split(" ");
		this.key = arrayToken[0];
		this.userEmail = arrayToken[1];
	}
	
	public EncryptedAuthToken encrypt() {
		byte[] encryptedToken = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, AutoUpdatingKey.getKey());
			encryptedToken = cipher.doFinal(token.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new EncryptedAuthToken(DatatypeConverter.printHexBinary(encryptedToken));
	}
	
}
