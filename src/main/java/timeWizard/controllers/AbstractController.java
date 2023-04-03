package timeWizard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.CrossOrigin;
import timeWizard.DAOLayer.Dao;
import timeWizard.DAOLayer.MainDao;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;

import javax.crypto.BadPaddingException;

@CrossOrigin(origins="http://localhost:8080")
public abstract class AbstractController {
    protected Dao dao;
    @Autowired
    AbstractController(@Qualifier("mainDao") Dao dao){
        this.dao = dao;
    }

    protected String getUserEmail(String token) throws BadPaddingException{
        EncryptedAuthToken encryptedAuthToken = new EncryptedAuthToken(token);
        AuthToken decryptedToken = encryptedAuthToken.decrypt();
        return decryptedToken.getUserEmail();

    }
}
