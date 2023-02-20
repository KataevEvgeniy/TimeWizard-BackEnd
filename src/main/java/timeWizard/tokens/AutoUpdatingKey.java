package timeWizard.tokens;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.crypto.KeyGenerator;

public class AutoUpdatingKey {


	static private final AutoUpdatingKey instance = new AutoUpdatingKey();
	private Key key;
	private Calendar nextKeyUpdate = new GregorianCalendar();

	private int daysAhead = 7;

	private AutoUpdatingKey(){}
	
	{
		nextKeyUpdate.set(Calendar.HOUR, 0);
		nextKeyUpdate.set(Calendar.MINUTE, 0);
		nextKeyUpdate.set(Calendar.SECOND, 0);
		nextKeyUpdate.set(Calendar.MILLISECOND, 0);
		nextKeyUpdate.add(Calendar.DAY_OF_YEAR, daysAhead);

		try {
			key = generateKey();
		} catch (NoSuchAlgorithmException e) {}
	}
	
	public Key getKey() {
		if ((new GregorianCalendar()).before(nextKeyUpdate)) {
			return key;
		}
		nextKeyUpdate.add(Calendar.DAY_OF_YEAR, daysAhead);
		try {
		    key = generateKey();
		} catch (NoSuchAlgorithmException e) {}
		return key;
	}

	private Key generateKey() throws NoSuchAlgorithmException{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(256);
		key = keyGenerator.generateKey();
		return key;
	}

	public static AutoUpdatingKey getInstance(){
		return instance;
	}
}
