package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.CommentPageResponse;
import com.pkg.tasktracker.dto.CommentResponse;
import com.pkg.tasktracker.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest {
    private CommentService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(CommentService.class);
        mvc = MockMvcBuilders.standaloneSetup(new CommentController(service))
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test void addComment() throws Exception {
        when(service.addComment(eq(5L), eq("Looks good")))
                .thenReturn(new CommentResponse(1L, "Looks good", 2L, "User", 5L, null));
        mvc.perform(post("/api/v1/tasks/5/comments").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Looks good\"}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.commentId").value(1));
    }

    @Test void listComments() throws Exception {
        when(service.getComments(eq(5L), any())).thenReturn(new CommentPageResponse(List.of(), 0, 20, 0, 0));
        mvc.perform(get("/api/v1/tasks/5/comments")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test void deleteComment() throws Exception {
        mvc.perform(delete("/api/v1/tasks/5/comments/1")).andExpect(status().isNoContent());
        verify(service).deleteComment(5L, 1L);
    }
}
