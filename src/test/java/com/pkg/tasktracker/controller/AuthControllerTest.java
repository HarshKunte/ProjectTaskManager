package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.dto.AuthResponse;
import com.pkg.tasktracker.dto.UserResponse;
import com.pkg.tasktracker.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {
    private AuthService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(AuthService.class);
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(service)).build();
    }

    @Test
    void signupCreatesUser() throws Exception {
        when(service.signUp(any())).thenReturn(new UserResponse(1L, "test@example.com", "Test User"));
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"fullName\":\"Test User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void signupRejectsInvalidRequest() throws Exception {
        mvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"bad\",\"password\":\"short\",\"fullName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginReturnsToken() throws Exception {
        when(service.login(any())).thenReturn(new AuthResponse("jwt-token", "Bearer", 3600000));
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void logoutRevokesToken() throws Exception {
        mvc.perform(post("/auth/logout").header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk());
        verify(service).logout("Bearer jwt-token");
    }
}
