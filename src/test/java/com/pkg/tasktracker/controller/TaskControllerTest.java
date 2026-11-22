package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.TaskPageResponse;
import com.pkg.tasktracker.dto.TaskResponse;
import com.pkg.tasktracker.entity.Priority;
import com.pkg.tasktracker.entity.TaskStatus;
import com.pkg.tasktracker.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest {
    private TaskService service;
    private MockMvc mvc;
    private TaskResponse response;

    @BeforeEach
    void setUp() {
        service = mock(TaskService.class);
        mvc = MockMvcBuilders.standaloneSetup(new TaskController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        response = new TaskResponse(5L, 10L, "Implement API", "Details", TaskStatus.OPEN,
                Priority.HIGH, null, null, null, 1L, Instant.now(), null, null);
    }

    @Test void createTask() throws Exception {
        when(service.createTask(eq(10L), any())).thenReturn(response);
        mvc.perform(post("/api/v1/projects/10/tasks").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Implement API\",\"priority\":\"HIGH\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(5));
    }

    @Test void listProjectTasks() throws Exception {
        when(service.listProjectTasks(eq(10L), any(), any(), any(), any(), any()))
                .thenReturn(new TaskPageResponse(List.of(response), 0, 20, 1, 1));
        mvc.perform(get("/api/v1/projects/10/tasks")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(5));
    }

    @Test void listMine() throws Exception {
        when(service.listMine(any(), any())).thenReturn(new TaskPageResponse(List.of(), 0, 20, 0, 0));
        mvc.perform(get("/api/v1/tasks/mine")).andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray());
    }

    @Test void getTask() throws Exception {
        when(service.getTask(5L)).thenReturn(response);
        mvc.perform(get("/api/v1/tasks/5")).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("Implement API"));
    }

    @Test void updateTask() throws Exception {
        when(service.updateTask(eq(5L), any())).thenReturn(response);
        mvc.perform(put("/api/v1/tasks/5").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated title\",\"priority\":\"MEDIUM\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(5));
    }

    @Test void assignTask() throws Exception {
        when(service.assignTask(eq(5L), any())).thenReturn(response);
        mvc.perform(patch("/api/v1/tasks/5/assignee").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assigneeId\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(5));
    }

    @Test void assignTaskRejectsMissingAssignee() throws Exception {
        mvc.perform(patch("/api/v1/tasks/5/assignee").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test void changeStatus() throws Exception {
        when(service.changeStatus(eq(5L), any())).thenReturn(response);
        mvc.perform(patch("/api/v1/tasks/5/status").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"COMPLETED\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(5));
    }

    @Test void deleteTask() throws Exception {
        mvc.perform(delete("/api/v1/tasks/5")).andExpect(status().isNoContent());
        verify(service).deleteTask(5L);
    }
}
