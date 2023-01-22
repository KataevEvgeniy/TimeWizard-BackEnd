package timeWizard.tokens;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EncryptedAuthToken {
	private String encryptedStringToken;
	
	public boolean isTrue() throws BadPaddingException{
		AuthToken token = decrypt();
		if(token == null)
			throw new BadPaddingException();
		if(token.getKey().equals(DatatypeConverter.printHexBinary(AutoUpdatingKey.getKey().getEncoded())))
			return true;
		
		return false;
	}
	
	public AuthToken decrypt() throws BadPaddingException {
		byte[] decryptedToken = new byte[0];
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, AutoUpdatingKey.getKey());
			decryptedToken = cipher.doFinal(DatatypeConverter.parseHexBinary(this.encryptedStringToken));
			return new AuthToken(new String(decryptedToken,"UTF-8"));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return encryptedStringToken;
	}
	
}
