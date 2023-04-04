package timeWizard.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import timeWizard.DAOLayer.Dao;
import timeWizard.config.SpringConfig;
import timeWizard.controllers.AuthController;
import org.junit.jupiter.api.Test;
import timeWizard.entity.CalendarTask;
import timeWizard.entity.User;
import timeWizard.tokens.AuthToken;
import timeWizard.tokens.AutoUpdatingKey;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Dao mockDao;

    User user = new User("name","email@mail.com","XXXXXXXX");
    CalendarTask calendarTask = new CalendarTask(1,"title","definition","email@mail.com",
            new Date(),new Date(),true,"#000000",1,"day");


    private HttpHeaders getHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", new AuthToken(user).encrypt().toString());
        headers.add("Access-Control-Expose-Headers", "Authorization");
        return headers;
    }

    @Test
    public void saveCalendarTask_whenValidInput_thenReturns201() throws Exception{
            doNothing().when(mockDao).create(calendarTask);
            mockMvc.perform(post("/saveCalendarTask")
                            .contentType(MediaType.APPLICATION_JSON)
                            .headers(getHttpHeaders())
                            .content(new ObjectMapper().writeValueAsString(calendarTask)))
                    .andExpect(status().isCreated())
                    .andExpect(content().string(new ObjectMapper().writeValueAsString(calendarTask)));
    }

    @Test
    public void saveCalendarTask_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/saveCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void saveCalendarTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).create(any());

        mockMvc.perform(post("/saveCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't created"));
    }

    @Test
    public void updateCalendarTask_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).update(calendarTask);
        mockMvc.perform(post("/updateCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(calendarTask)));
    }

    @Test
    public void updateCalendarTask_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/updateCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void updateCalendarTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).update(any());

        mockMvc.perform(post("/updateCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't update"));
    }

    @Test
    public void deleteCalendarTask_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).update(calendarTask);
        mockMvc.perform(post("/deleteCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task deleted"));
    }

    @Test
    public void deleteCalendarTask_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/deleteCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void deleteCalendarTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).delete(any());

        mockMvc.perform(post("/deleteCalendarTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't delete"));
    }

    @Test
    public void getAllCalendarTasks_whenValidInput_thenReturns200() throws Exception{
        List<CalendarTask> list = new ArrayList<>();
        list.add(calendarTask);
        doReturn(list).when(mockDao).readAll(CalendarTask.class,"email@mail.com");

        mockMvc.perform(get("/getAllCalendarTasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders()))
                .andExpect(status().isAccepted())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(list)));
    }

    @Test
    public void getAllCalendarTasks_whenInvalidInput_thenReturns400() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(get("/getAllCalendarTasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(calendarTask)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }
}
