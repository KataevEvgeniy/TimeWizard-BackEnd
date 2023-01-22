package timeWizard.tokens;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.KeyGenerator;

public class AutoUpdatingKey {
	static private Key key;
	static private Calendar tokenUpdateDate = new GregorianCalendar();
	
	static {
		tokenUpdateDate.set(Calendar.HOUR, 0);
		tokenUpdateDate.set(Calendar.MINUTE, 0);
		tokenUpdateDate.set(Calendar.SECOND, 0);
		tokenUpdateDate.set(Calendar.MILLISECOND, 0);
		tokenUpdateDate.add(Calendar.DAY_OF_YEAR, 1);
		
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
		    key = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {}
	}
	
	public static Key getKey() {
		if ((new GregorianCalendar()).before(tokenUpdateDate)) 
			return key;
		
		tokenUpdateDate.add(Calendar.DAY_OF_YEAR, 1);
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
		    key = keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {}
		return key;
	}
	
}
