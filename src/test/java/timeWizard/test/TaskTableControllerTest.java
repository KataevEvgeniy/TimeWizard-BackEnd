package timeWizard.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
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
import timeWizard.controllers.TaskTableController;
import timeWizard.entity.CalendarTask;
import timeWizard.entity.TableColumn;
import timeWizard.entity.TableTask;
import timeWizard.entity.User;
import timeWizard.tokens.AuthToken;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskTableController.class)
@ContextConfiguration(classes = {SpringConfig.class})
public class TaskTableControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Dao mockDao;

    User user = new User("name","email@mail.com","XXXXXXXX");
    TableColumn tableColumn = new TableColumn(1,"title","email@mail.com");
    TableTask tableTask = new TableTask(1,"text",tableColumn);

    private HttpHeaders getHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", new AuthToken(user).encrypt().toString());
        headers.add("Access-Control-Expose-Headers", "Authorization");
        return headers;
    }

    @Test
    public void saveTableTask_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).create(tableTask);
        mockMvc.perform(post("/saveTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task created"));
    }


    @Test
    public void saveTableTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).create(any());

        mockMvc.perform(post("/saveTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't create"));
    }

    @Test
    public void updateTableTask_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).update(tableTask);
        mockMvc.perform(post("/updateTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task updated"));
    }


    @Test
    public void updateTableTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).update(any());

        mockMvc.perform(post("/updateTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't update"));
    }

    @Test
    public void deleteTableTask_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).delete(tableTask);
        mockMvc.perform(post("/deleteTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task deleted"));
    }


    @Test
    public void deleteTableTask_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).delete(any());

        mockMvc.perform(post("/deleteTableTask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableTask)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Task didn't delete"));
    }

    @Test
    public void getAllTableTasks_whenValidInput_thenReturns200() throws Exception{
        List<TableTask> list = new ArrayList<>();
        list.add(tableTask);
        doReturn(list).when(mockDao).readAll(TableTask.class,tableColumn);

        mockMvc.perform(post("/getAllTableTasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(list)));
    }

    @Test
    public void saveTableColumn_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).create(tableColumn);
        mockMvc.perform(post("/saveTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Column created"));
    }

    @Test
    public void saveTableColumn_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/saveTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void saveTableColumn_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).create(any());

        mockMvc.perform(post("/saveTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Column didn't create"));
    }

    @Test
    public void updateTableColumn_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).update(tableColumn);
        mockMvc.perform(post("/updateTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Column updated"));
    }

    @Test
    public void updateTableColumn_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/updateTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void updateTableColumn_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).update(any());

        mockMvc.perform(post("/updateTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Column didn't update"));
    }

    @Test
    public void deleteTableColumn_whenValidInput_thenReturns201() throws Exception{
        doNothing().when(mockDao).delete(tableColumn);
        mockMvc.perform(post("/deleteTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Column deleted"));
    }

    @Test
    public void deleteTableColumn_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(post("/deleteTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }

    @Test
    public void deleteTableColumn_whenInvalidInput_thenReturns400() throws Exception{
        doThrow(SQLDataException.class).when(mockDao).delete(any());

        mockMvc.perform(post("/deleteTableColumn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders())
                        .content(new ObjectMapper().writeValueAsString(tableColumn)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Column didn't delete"));
    }

    @Test
    public void getAllTableColumns_whenValidInput_thenReturns200() throws Exception{
        List<TableColumn> list = new ArrayList<>();
        list.add(tableColumn);
        doReturn(list).when(mockDao).readAll(TableColumn.class,"email@mail.com");

        mockMvc.perform(get("/getAllTableColumns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHttpHeaders()))
                .andExpect(status().isAccepted())
                .andExpect(content().string(new ObjectMapper().writeValueAsString(list)));
    }

    @Test
    public void getAllTableColumns_whenInvalidInput_thenReturns401() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Access-Control-Expose-Headers", "Authorization");
        mockMvc.perform(get("/getAllTableColumns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token is expired"));
    }
}
