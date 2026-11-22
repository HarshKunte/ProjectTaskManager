package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.ProjectMembersResponse;
import com.pkg.tasktracker.entity.Project;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest {
    private ProjectService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(ProjectService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ProjectController(service)).build();
    }

    @Test
    void createProject() throws Exception {
        User owner = new User(); owner.setId(1L); owner.setEmail("owner@example.com");
        Project project = new Project(); project.setId(10L); project.setName("Website"); project.setDescription("Migration"); project.setOwner(owner);
        when(service.createProject(any())).thenReturn(project);

        mvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Website\",\"description\":\"Migration\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Website"));
    }

    @Test
    void listMyProjects() throws Exception {
        when(service.getMyProjects()).thenReturn(List.of());
        mvc.perform(get("/projects")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
    }

    @Test
    void getProjectDetails() throws Exception {
        when(service.getProjectDetailsAndMembers(10L)).thenReturn(new ProjectMembersResponse(10L, "Website", "Migration", 1L, List.of()));
        mvc.perform(get("/projects/10")).andExpect(status().isOk()).andExpect(jsonPath("$.projectId").value(10));
    }

    @Test
    void addMember() throws Exception {
        mvc.perform(post("/projects/10/members").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"member@example.com\"}"))
                .andExpect(status().isOk());
        verify(service).addMember(10L, "member@example.com");
    }

    @Test
    void removeMember() throws Exception {
        mvc.perform(delete("/projects/10/members/2")).andExpect(status().isOk());
        verify(service).removeMember(10L, 2L);
    }
}
