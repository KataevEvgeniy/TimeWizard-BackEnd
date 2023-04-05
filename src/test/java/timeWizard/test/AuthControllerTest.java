package timeWizard.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import timeWizard.DAOLayer.Dao;
import timeWizard.config.SpringConfig;
import timeWizard.controllers.AuthController;
import timeWizard.entity.User;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.EncryptedAuthToken;

import java.sql.SQLDataException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Dao mockDao;


    @Test
    public void register_whenValidInput_thenReturns201() throws Exception {
        User user = new User("user","user@email.com","qwerty");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Login is accept"))
                .andExpect(header().string("Authorization", new AuthToken(user).encrypt().toString()))
                .andExpect(header().string("Access-Control-Expose-Headers", "Authorization"));
    }

    @Test
    public void register_whenInvalidInput_thenReturns401() throws Exception {
        User user = new User("user","user@email.com","qwerty");

        doThrow(SQLDataException.class).when(mockDao).create(any());

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User already registered"));
    }

    @Test
    public void register_whenInvalidInput_thenReturns400() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void login_whenValidInput_thenReturns201() throws Exception {
        User loggingUser = new User("user","user@email.com","qwerty");
        User dbUser = new User("user","user@email.com","qwerty");
        dbUser.encryptPassword();

        doReturn(dbUser).when(mockDao).read(User.class,loggingUser.getEmail());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loggingUser)))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Login is accept"))
                .andExpect(header().string("Authorization", new AuthToken(loggingUser).encrypt().toString()))
                .andExpect(header().string("Access-Control-Expose-Headers", "Authorization"));
    }

    @Test
    public void login_whenInvalidInput_thenReturns401() throws Exception {
        User user = new User("user","user@email.com","qwerty");

        doReturn(null).when(mockDao).read(User.class,user.getEmail());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Login failed"));
    }

    @Test
    public void login_whenInvalidInput_thenReturns400() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkToken_whenValidInput_thenReturns202() throws Exception {
        User user = new User("user","user@email.com","qwerty");
        EncryptedAuthToken  encryptedAuthToken = new AuthToken(user).encrypt();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", encryptedAuthToken.toString());
        headers.add("Access-Control-Expose-Headers", "Authorization");

        mockMvc.perform(get("/checkToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers))
                .andExpect(status().isAccepted())
                .andExpect(content().string("Token is true"));
    }

    @Test
    public void checkToken_whenInvalidInput_thenReturns401() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(get("/checkToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void checkToken_whenInvalidInput_thenReturns400() throws Exception {
        mockMvc.perform(get("/checkToken")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
