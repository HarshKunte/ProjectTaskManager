package com.pkg.tasktracker.controller;

import com.pkg.tasktracker.controller.helper.CurrentUser;
import com.pkg.tasktracker.entity.User;
import com.pkg.tasktracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        CurrentUser currentUser = mock(CurrentUser.class);
        UserService userService = mock(UserService.class);
        UserController controller = new UserController();
        ReflectionTestUtils.setField(controller, "currentUser", currentUser);
        ReflectionTestUtils.setField(controller, "userService", userService);

        User user = new User();
        user.setId(1L); user.setEmail("test@example.com"); user.setFullName("Test User");
        when(currentUser.get()).thenReturn(user);
        when(userService.updateUser(any(User.class), any())).thenReturn(user);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getCurrentUser() throws Exception {
        mvc.perform(get("/users/me")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateCurrentUser() throws Exception {
        mvc.perform(put("/users/me").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    void updateCurrentUserRejectsBlankName() throws Exception {
        mvc.perform(put("/users/me").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
