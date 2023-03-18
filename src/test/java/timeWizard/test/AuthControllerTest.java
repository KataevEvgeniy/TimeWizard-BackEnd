package timeWizard.test;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import timeWizard.config.SpringConfig;
import timeWizard.controllers.AuthController;
import timeWizard.entity.User;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.AutoUpdatingKey;
import timeWizard.tokens.EncryptedAuthToken;


import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private AutoUpdatingKey autoUpdatingKey;

    @Test
    public void whenValidInput_thenReturns200() throws Exception {
        User user = new User("user","user4@email.com","qwerty");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", new AuthToken(user).encrypt().toString());
        headers.add("Access-Control-Expose-Headers", "Authorization");

        assertEquals(1,1);
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Login is accept"))
                .andExpect(header().string("Authorization", new AuthToken(user).encrypt().toString()))
                .andExpect(header().string("Access-Control-Expose-Headers", "Authorization"));
    }
}
