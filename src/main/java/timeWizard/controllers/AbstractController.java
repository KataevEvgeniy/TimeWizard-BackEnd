package timeWizard.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import timeWizard.DAOLayer.MainDao;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;

import javax.crypto.BadPaddingException;

@CrossOrigin(origins="http://localhost:8080")
public abstract class AbstractController {

    protected MainDao dao = new MainDao();
    protected String getUserEmail(String token){
        try {
            EncryptedAuthToken encryptedAuthToken = new EncryptedAuthToken(token);
            AuthToken decryptedToken = encryptedAuthToken.decrypt();
            return decryptedToken.getUserEmail();
        } catch (BadPaddingException e) {
            return null;
        }
    }
}
